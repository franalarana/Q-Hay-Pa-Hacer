package cl.quehaypahacer.backend.config.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuración de infraestructura para Apache Kafka (Streaming y Analítica).
 * <p>
 * Declara como beans:
 * - Los 2 tópicos requeridos por la rúbrica (con particiones, réplicas y retención) y sus DLT.
 * - El cliente AdminClient para operaciones administrativas en caliente.
 * - El contenedor de listeners con control de reintentos, DLT y logging contextual.
 */
@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    /** Sufijo por defecto que usa DeadLetterPublishingRecoverer para el tópico de mensajes fallidos. */
    public static final String DLT_SUFFIX = ".DLT";

    /** Los mensajes fallidos se guardan 30 días para poder investigarlos. */
    private static final long DLT_RETENTION_MS = 2_592_000_000L;

    private final KafkaProperties props;

    public KafkaConfig(KafkaProperties props) {
        this.props = props;
    }

    /**
     * Tópico 1: Flujo analítico de ingredientes registrados en despensa.
     */
    @Bean
    public NewTopic topicIngredientesDespensa() {
        return TopicBuilder.name(props.topics().ingredientesDespensa())
                .partitions(props.partitions())
                .replicas(props.replicationFactor())
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(props.retentionMs()))
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, props.cleanupPolicy())
                .build();
    }

    /**
     * Tópico 2: Flujo analítico de ingredientes consumidos al cocinar recetas.
     */
    @Bean
    public NewTopic topicIngredientesCocina() {
        return TopicBuilder.name(props.topics().ingredientesCocina())
                .partitions(props.partitions())
                .replicas(props.replicationFactor())
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(props.retentionMs()))
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, props.cleanupPolicy())
                .build();
    }

    /** DLT de despensa: mismas particiones que el original (el recoverer conserva el número de partición). */
    @Bean
    public NewTopic topicIngredientesDespensaDlt() {
        return construirDlt(props.topics().ingredientesDespensa());
    }

    @Bean
    public NewTopic topicIngredientesCocinaDlt() {
        return construirDlt(props.topics().ingredientesCocina());
    }

    private NewTopic construirDlt(String nombreOriginal) {
        return TopicBuilder.name(nombreOriginal + DLT_SUFFIX)
                .partitions(props.partitions())
                .replicas(props.replicationFactor())
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(DLT_RETENTION_MS))
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }

    /**
     * Bean de KafkaAdmin para declarar automáticamente los NewTopic al iniciar.
     */
    @Bean
    public KafkaAdmin kafkaAdmin(@Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setFatalIfBrokerNotAvailable(false);
        return admin;
    }

    /**
     * Bean de AdminClient para gestión programática de tópicos y consumer groups.
     */
    @Bean
    public AdminClient kafkaAdminClient(@Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(AdminClientConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);
        return AdminClient.create(configs);
    }

    /**
     * Factory de productores para serialización JSON (Jackson 3 / Spring Kafka 4).
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        configs.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        // Confiabilidad: espera confirmación de todas las réplicas y evita duplicados en reintentos
        configs.put(ProducerConfig.ACKS_CONFIG, "all");
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        // Aislamiento ante caída del broker: send() nunca bloquea el hilo más de 3 s
        // (por defecto son 60 s), así la petición HTTP y los flujos RabbitMQ no quedan colgados
        configs.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 3000);
        configs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        configs.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000);
        configs.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    /**
     * Plantilla tipada KafkaTemplate para publicar eventos a los tópicos.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Factory de consumidores para deserialización JSON tipada con manejo de errores (Jackson 3 / Spring Kafka 4).
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configs.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configs.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Si el broker no está disponible, reintenta conexión con backoff creciente hasta 10 s
        // en vez de cada 1 s, evitando saturar el log (y los logs de RabbitMQ) con advertencias
        configs.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);
        // Solo se deserializan clases de nuestros paquetes de eventos (sin comodín "*")
        configs.put(JacksonJsonDeserializer.TRUSTED_PACKAGES,
                "cl.quehaypahacer.backend.messaging.kafka.event,cl.quehaypahacer.backend.messaging.event");
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    /**
     * Recoverer que envía a "<tópico>.DLT" los mensajes que fallaron tras los reintentos.
     * - Si el fallo fue de deserialización, el valor original llega como byte[] → plantilla de bytes.
     * - Cualquier otro valor (evento ya deserializado) → plantilla JSON normal.
     * Agrega headers con la excepción, tópico, partición y offset originales.
     */
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {
        Map<String, Object> configBytes = new HashMap<>(kafkaTemplate.getProducerFactory().getConfigurationProperties());
        configBytes.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        configBytes.remove(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS);
        KafkaTemplate<String, byte[]> plantillaBytes = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configBytes));

        Map<Class<?>, KafkaOperations<?, ?>> plantillas = new LinkedHashMap<>();
        plantillas.put(byte[].class, plantillaBytes);
        plantillas.put(Object.class, kafkaTemplate);
        return new DeadLetterPublishingRecoverer(plantillas);
    }

    /**
     * Factory de contenedores de consumo con política de manejo de errores, reintentos y DLT (Indicador 6).
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            log.error("[KAFKA ERROR] Fallo definitivo en tópico '{}', partición '{}', offset '{}': {} → enviando a {}{}",
                    record.topic(), record.partition(), record.offset(), exception.getMessage(),
                    record.topic(), DLT_SUFFIX);
            deadLetterPublishingRecoverer.accept(record, exception);
        }, new FixedBackOff(1000L, 2L));
        // Un payload inválido no se arregla reintentando: directo a la DLT
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
