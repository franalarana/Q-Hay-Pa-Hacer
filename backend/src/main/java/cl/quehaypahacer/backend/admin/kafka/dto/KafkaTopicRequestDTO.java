package cl.quehaypahacer.backend.admin.kafka.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para la creación y configuración de tópicos Kafka vía API REST.
 */
public record KafkaTopicRequestDTO(
        @NotBlank(message = "El nombre del tópico no puede estar vacío")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Solo letras, números, punto, guion y guion bajo")
        String name,

        @Min(value = 1, message = "Debe tener al menos 1 partición")
        int partitions,

        @Min(value = 1, message = "El factor de replicación debe ser al menos 1")
        short replicationFactor,

        Long retentionMs,

        String cleanupPolicy
) {}
