package cl.quehaypahacer.backend.admin.kafka.dto;

/**
 * Estado de una partición dentro de un consumer group.
 * lag = endOffset - committedOffset (mensajes que el grupo aún no procesa).
 */
public record KafkaPartitionOffsetDTO(
        String topic,
        int partition,
        long committedOffset,
        long endOffset,
        long lag,
        String consumerId
) {}
