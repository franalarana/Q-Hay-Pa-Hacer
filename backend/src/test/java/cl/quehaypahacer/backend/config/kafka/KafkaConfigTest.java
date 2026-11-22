package cl.quehaypahacer.backend.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica, sin broker, que la configuración del cliente Kafka y de los tópicos
 * declara los parámetros que exige la arquitectura (indicadores 1, 6 y 7).
 */
class KafkaConfigTest {

    private static final String BOOTSTRAP = "localhost:9092,localhost:9093,localhost:9094";

    private final KafkaProperties props = new KafkaProperties(
            new KafkaProperties.Topics("analitica.despensa.ingredientes", "analitica.cocina.ingredientes"),
            (short) 3,
            2,
            new KafkaProperties.FlujoConfig(3, 604800000L, "delete"),
            new KafkaProperties.FlujoConfig(2, 2592000000L, "delete"),
            2,
            new KafkaProperties.ConsumerGroups("quehaypahacer-analitica-group", "quehaypahacer-auditoria-group")
    );

    private final KafkaConfig config = new KafkaConfig(props);

    @Test
    void topicoDespensaUsaSuPropiaConfiguracion() {
        NewTopic topic = config.topicIngredientesDespensa();

        assertThat(topic.name()).isEqualTo("analitica.despensa.ingredientes");
        assertThat(topic.numPartitions()).isEqualTo(3);
        assertThat(topic.replicationFactor()).isEqualTo((short) 3);
        assertThat(topic.configs())
                .containsEntry(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .containsEntry(TopicConfig.CLEANUP_POLICY_CONFIG, "delete")
                .containsEntry(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2");
    }

    @Test
    void topicoCocinaUsaSuPropiaConfiguracion() {
        NewTopic topic = config.topicIngredientesCocina();

        assertThat(topic.name()).isEqualTo("analitica.cocina.ingredientes");
        assertThat(topic.numPartitions()).isEqualTo(2);
        assertThat(topic.configs()).containsEntry(TopicConfig.RETENTION_MS_CONFIG, "2592000000");
    }

    @Test
    void losFlujosTienenRetencionDistinta() {
        assertThat(config.topicIngredientesDespensa().configs().get(TopicConfig.RETENTION_MS_CONFIG))
                .isNotEqualTo(config.topicIngredientesCocina().configs().get(TopicConfig.RETENTION_MS_CONFIG));
    }

    @Test
    void cadaTopicoTieneSuDltConLasMismasParticiones() {
        NewTopic dltDespensa = config.topicIngredientesDespensaDlt();
        NewTopic dltCocina = config.topicIngredientesCocinaDlt();

        assertThat(dltDespensa.name()).isEqualTo("analitica.despensa.ingredientes.DLT");
        assertThat(dltDespensa.numPartitions()).isEqualTo(config.topicIngredientesDespensa().numPartitions());
        assertThat(dltCocina.name()).isEqualTo("analitica.cocina.ingredientes.DLT");
        assertThat(dltCocina.numPartitions()).isEqualTo(config.topicIngredientesCocina().numPartitions());
    }

    @Test
    void productorUsaSerializersJsonYConfiabilidad() {
        ProducerFactory<String, Object> factory = config.producerFactory(BOOTSTRAP);
        Map<String, Object> cfg = factory.getConfigurationProperties();

        assertThat(cfg).containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        assertThat(cfg).containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        assertThat(cfg).containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        assertThat(cfg).containsEntry(ProducerConfig.ACKS_CONFIG, "all");
        assertThat(cfg).containsEntry(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    }

    @Test
    void productorNoBloqueaMasDeTresSegundosSiElBrokerCae() {
        Map<String, Object> cfg = config.producerFactory(BOOTSTRAP).getConfigurationProperties();

        // Protege los flujos RabbitMQ y la petición HTTP ante una caída de Kafka
        assertThat(cfg).containsEntry(ProducerConfig.MAX_BLOCK_MS_CONFIG, 3000);
        assertThat((Integer) cfg.get(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG))
                .isGreaterThanOrEqualTo((Integer) cfg.get(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG));
    }

    @Test
    void consumidorEnvuelveDeserializadoresYSoloConfiaEnNuestrosPaquetes() {
        ConsumerFactory<String, Object> factory = config.consumerFactory(BOOTSTRAP);
        Map<String, Object> cfg = factory.getConfigurationProperties();

        assertThat(cfg).containsEntry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        assertThat(cfg).containsEntry(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
        assertThat(cfg).containsEntry(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        assertThat((String) cfg.get(JacksonJsonDeserializer.TRUSTED_PACKAGES)).doesNotContain("*");
    }

    @Test
    void contenedorDeListenersTieneErrorHandlerConDlt() {
        KafkaTemplate<String, Object> template = config.kafkaTemplate(config.producerFactory(BOOTSTRAP));
        DeadLetterPublishingRecoverer recoverer = config.deadLetterPublishingRecoverer(template);
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                config.kafkaListenerContainerFactory(config.consumerFactory(BOOTSTRAP), recoverer);

        assertThat(factory.getConsumerFactory()).isNotNull();
        assertThat(factory).extracting("commonErrorHandler").isInstanceOf(DefaultErrorHandler.class);
    }
}
