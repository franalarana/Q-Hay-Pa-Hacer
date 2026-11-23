package cl.quehaypahacer.backend.admin.kafka.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * DTO para modificar en caliente la configuración de un tópico existente.
 * Ambos campos son opcionales, pero debe venir al menos uno.
 */
public record KafkaTopicConfigUpdateDTO(
        @Positive(message = "La retención debe ser mayor a 0 ms")
        Long retentionMs,

        @Pattern(regexp = "^(delete|compact|compact,delete|delete,compact)$",
                message = "cleanupPolicy debe ser delete, compact o compact,delete")
        String cleanupPolicy
) {}
