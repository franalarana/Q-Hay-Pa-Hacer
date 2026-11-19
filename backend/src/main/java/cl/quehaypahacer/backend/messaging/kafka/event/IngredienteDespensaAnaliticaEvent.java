package cl.quehaypahacer.backend.messaging.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Evento analítico transmitido por Kafka cuando un usuario interactúa con su despensa.
 * Permite analizar la frecuencia y stock de ingredientes en los hogares en tiempo real.
 */
public record IngredienteDespensaAnaliticaEvent(
        @NotNull Long ingredienteId,
        @NotBlank String nombreIngrediente,
        Double cantidad,
        String unidad,
        @NotBlank String accion,
        String usuarioCorreo,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
    public static class Accion {
        public static final String AGREGADO = "AGREGADO";
        public static final String ACTUALIZADO = "ACTUALIZADO";
        public static final String ELIMINADO = "ELIMINADO";
    }
}
