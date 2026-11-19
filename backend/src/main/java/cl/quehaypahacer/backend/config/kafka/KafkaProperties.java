package cl.quehaypahacer.backend.config.kafka;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Propiedades centralizadas y validadas para Apache Kafka (Streaming y Analítica).
 * Mapea la sección {@code app.kafka.*} de application.yml.
 * Garantiza que ninguna clase maneje nombres de tópicos o consumer groups quemados en código.
 */
@Validated
@ConfigurationProperties(prefix = "app.kafka")
public record KafkaProperties(
        @Valid @NotNull Topics topics,
        @NotNull @Min(1) Integer partitions,
        @NotNull @Min(1) Short replicationFactor,
        @NotNull Long retentionMs,
        @NotBlank String cleanupPolicy,
        @Valid @NotNull ConsumerGroups consumerGroups
) {

    public record Topics(
            @NotBlank String ingredientesDespensa,
            @NotBlank String ingredientesCocina
    ) {}

    public record ConsumerGroups(
            @NotBlank String analitica,
            @NotBlank String auditoria
    ) {}
}
