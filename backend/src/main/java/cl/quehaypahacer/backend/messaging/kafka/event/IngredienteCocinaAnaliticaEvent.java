package cl.quehaypahacer.backend.messaging.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Evento analítico transmitido por Kafka cuando se cocina una receta y se consumen ingredientes.
 * Permite calcular qué ingredientes son los más cocinados/usados por la comunidad en tiempo real.
 */
public record IngredienteCocinaAnaliticaEvent(
        @NotNull Long recetaId,
        @NotBlank String recetaTitulo,
        @NotNull Long ingredienteId,
        @NotBlank String nombreIngrediente,
        Double cantidad,
        String unidad,
        String usuarioCorreo,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {}
