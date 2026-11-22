package cl.quehaypahacer.backend.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
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

    private static final String BOOTSTRAP = "localhost:9092";

    private final KafkaProperties props = new KafkaProperties(
            new KafkaProperties.Topics("analitica.despensa.ingredientes", "analitica.cocina.ingredientes"),
            2,
            (short) 1,
            604800000L,
            "delete",
            new KafkaProperties.ConsumerGroups("quehaypahacer-analitica-group", "quehaypahacer-auditoria-group")
    );

    private final KafkaConfig config = new KafkaConfig(props);

    @Test
    void topicoDespensaTieneParticionesReplicasRetencionYCleanup() {
        NewTopic topic = config.topicIngredientesDespensa();

        assertThat(topic.name()).isEqualTo("analitica.despensa.ingredientes");
        assertThat(topic.numPartitions()).isEqualTo(2);
        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
        assertThat(topic.configs())
                .containsEntry(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .containsEntry(TopicConfig.CLEANUP_POLICY_CONFIG, "delete");
    }

    @Test
    void topicoCocinaTieneParticionesReplicasRetencionYCleanup() {
        NewTopic topic = config.topicIngredientesCocina();

        assertThat(topic.name()).isEqualTo("analitica.cocina.ingredientes");
        assertThat(topic.numPartitions()).isEqualTo(2);
        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
        assertThat(topic.configs()).containsKeys(TopicConfig.RETENTION_MS_CONFIG, TopicConfig.CLEANUP_POLICY_CONFIG);
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
    void consumidorEnvuelveDeserializadoresParaToleraPayloadsCorruptos() {
        ConsumerFactory<String, Object> factory = config.consumerFactory(BOOTSTRAP);
        Map<String, Object> cfg = factory.getConfigurationProperties();

        assertThat(cfg).containsEntry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        assertThat(cfg).containsEntry(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
        assertThat(cfg).containsEntry(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    @Test
    void contenedorDeListenersTieneErrorHandlerConReintentos() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                config.kafkaListenerContainerFactory(config.consumerFactory(BOOTSTRAP));

        assertThat(factory.getConsumerFactory()).isNotNull();
        assertThat(factory).extracting("commonErrorHandler").isInstanceOf(DefaultErrorHandler.class);
    }
}
