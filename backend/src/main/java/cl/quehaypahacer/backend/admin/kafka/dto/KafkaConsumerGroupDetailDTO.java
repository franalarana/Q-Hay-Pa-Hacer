package cl.quehaypahacer.backend.admin.kafka.dto;

import java.util.List;

/**
 * Detalle de un consumer group: miembros activos, offsets por partición y lag total.
 */
public record KafkaConsumerGroupDetailDTO(
        String groupId,
        String state,
        int members,
        long totalLag,
        List<KafkaPartitionOffsetDTO> partitions
) {}
