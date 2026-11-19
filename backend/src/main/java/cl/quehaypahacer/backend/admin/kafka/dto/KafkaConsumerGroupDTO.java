package cl.quehaypahacer.backend.admin.kafka.dto;

/**
 * DTO para la inspección de grupos de consumidores de Kafka vía API REST.
 */
public record KafkaConsumerGroupDTO(
        String groupId,
        String state,
        boolean isSimpleConsumerGroup
) {}
