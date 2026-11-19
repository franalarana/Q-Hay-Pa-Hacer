package cl.quehaypahacer.backend.config.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de infraestructura para Apache Kafka (Streaming y Analítica).
 * <p>
 * Declara como beans:
 * - Los 2 tópicos requeridos por la rúbrica (con particiones, réplicas y retención).
 * - El cliente AdminClient para operaciones administrativas en caliente.
 * - El contenedor de listeners con control de reintentos y logging contextual.
 */
@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

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
        configs.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "cl.quehaypahacer.backend.messaging.kafka.event,cl.quehaypahacer.backend.messaging.event,*");
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    /**
     * Factory de contenedores de consumo con política de manejo de errores y reintentos (Indicador 6).
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            log.error("[KAFKA ERROR] Fallo definitivo al procesar mensaje en tópico '{}', partición '{}', offset '{}': {}",
                    record.topic(), record.partition(), record.offset(), exception.getMessage());
        }, new FixedBackOff(1000L, 2L));

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
