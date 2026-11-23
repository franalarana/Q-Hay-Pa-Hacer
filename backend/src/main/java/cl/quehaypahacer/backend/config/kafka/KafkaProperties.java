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
 * Cada flujo de datos (despensa / cocina) tiene su propia configuración de tópico.
 */
@Validated
@ConfigurationProperties(prefix = "app.kafka")
public record KafkaProperties(
        @Valid @NotNull Topics topics,
        @NotNull @Min(1) Short replicationFactor,
        @NotNull @Min(1) Integer minInsyncReplicas,
        @Valid @NotNull FlujoConfig despensa,
        @Valid @NotNull FlujoConfig cocina,
        @NotNull @Min(1) Integer consumerConcurrency,
        @Valid @NotNull ConsumerGroups consumerGroups
) {

    public record Topics(
            @NotBlank String ingredientesDespensa,
            @NotBlank String ingredientesCocina
    ) {}

    /** Parámetros de administración de un tópico concreto. */
    public record FlujoConfig(
            @NotNull @Min(1) Integer partitions,
            @NotNull Long retentionMs,
            @NotBlank String cleanupPolicy
    ) {}

    public record ConsumerGroups(
            @NotBlank String analitica,
            @NotBlank String auditoria
    ) {}
}
