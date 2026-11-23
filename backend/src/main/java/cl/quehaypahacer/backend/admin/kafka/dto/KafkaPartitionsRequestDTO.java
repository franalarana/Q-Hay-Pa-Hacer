package cl.quehaypahacer.backend.admin.kafka.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para aumentar el número total de particiones de un tópico.
 * Kafka solo permite AUMENTAR particiones, nunca disminuirlas.
 */
public record KafkaPartitionsRequestDTO(
        @NotNull(message = "Debe indicar el total de particiones")
        @Min(value = 1, message = "Debe tener al menos 1 partición")
        Integer totalPartitions
) {}
