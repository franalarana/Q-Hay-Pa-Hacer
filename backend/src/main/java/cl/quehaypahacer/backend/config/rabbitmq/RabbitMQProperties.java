package cl.quehaypahacer.backend.config.rabbitmq;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Nombres centralizados de la mensajería RabbitMQ (Indicador 1).
 * <p>
 * Todos los valores se leen desde {@code app.rabbitmq.*} en application.yml.
 * Ninguna otra clase debe escribir nombres de colas, exchanges o routing keys
 * como texto literal: siempre se inyecta este bean.
 * <p>
 * Si falta algún valor, la aplicación no arranca (validación con @Validated).
 */
@Validated
@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMQProperties(
        @Valid @NotNull Exchanges exchanges,
        @Valid @NotNull Queues queues,
        @Valid @NotNull RoutingKeys routingKeys
) {

    /** Exchange principal de negocio y Dead Letter Exchange (DLX). */
    public record Exchanges(
            @NotBlank String principal,
            @NotBlank String deadLetter
    ) {
    }

    /** Colas de trabajo y sus colas de mensajes fallidos (DLQ). */
    public record Queues(
            @NotBlank String tickets,
            @NotBlank String notificaciones,
            @NotBlank String ticketsDlq,
            @NotBlank String notificacionesDlq
    ) {
    }

    /** Routing keys usadas por los bindings y por los publicadores. */
    public record RoutingKeys(
            @NotBlank String tickets,
            @NotBlank String notificaciones,
            @NotBlank String ticketsDlq,
            @NotBlank String notificacionesDlq
    ) {
    }
}
