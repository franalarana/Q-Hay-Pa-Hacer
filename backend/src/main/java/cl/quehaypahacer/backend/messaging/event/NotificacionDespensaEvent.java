package cl.quehaypahacer.backend.messaging.event;

import java.time.LocalDateTime;

/**
 * Evento publicado cuando cambia la despensa de un usuario
 * (agregar, actualizar cantidad o eliminar un ingrediente).
 * Lo consume NotificacionConsumer para emitir el aviso de forma asíncrona.
 *
 * @param ingredienteUsuarioId id del ítem en la despensa
 * @param ingredienteNombre    nombre del ingrediente
 * @param cantidad             cantidad resultante (null si se eliminó)
 * @param unidad               unidad de medida (texto del enum UnidadMedida)
 * @param accion               qué ocurrió en la despensa
 * @param usuarioCorreo        correo del dueño de la despensa
 * @param ocurridoAt           fecha/hora del cambio
 */
public record NotificacionDespensaEvent(
        Long ingredienteUsuarioId,
        String ingredienteNombre,
        Double cantidad,
        String unidad,
        Accion accion,
        String usuarioCorreo,
        LocalDateTime ocurridoAt
) {

    /** Tipo de cambio realizado en la despensa. */
    public enum Accion {
        AGREGADO,
        ACTUALIZADO,
        ELIMINADO
    }
}
