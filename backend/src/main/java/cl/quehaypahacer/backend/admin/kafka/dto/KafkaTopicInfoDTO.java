package cl.quehaypahacer.backend.admin.kafka.dto;

import java.util.Map;

/**
 * DTO con el estado y configuración de un tópico Kafka inspeccionado en caliente.
 */
public record KafkaTopicInfoDTO(
        String name,
        int partitions,
        int replicationFactor,
        Map<String, String> configs
) {}
