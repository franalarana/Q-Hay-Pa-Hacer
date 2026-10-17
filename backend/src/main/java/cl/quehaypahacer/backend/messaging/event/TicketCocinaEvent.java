package cl.quehaypahacer.backend.messaging.event;

import java.time.LocalDateTime;

/**
 * Evento publicado cuando un usuario registra que cocinó una receta
 * (POST /api/historial/{recetaId}). Lo consume TicketCocinaConsumer para
 * generar el ticket / ficha de cocinado de forma asíncrona.
 *
 * @param historialId  id del registro guardado en historial_cocina
 * @param recetaId     id de la receta cocinada (debe ser > 0; si no, el
 *                     consumidor lo considera inválido y lo envía a la DLQ)
 * @param recetaNombre nombre de la receta
 * @param usuarioCorreo correo del usuario que cocinó
 * @param notas        notas opcionales del usuario
 * @param cocinadoAt   fecha/hora del registro
 */
public record TicketCocinaEvent(
        Long historialId,
        Long recetaId,
        String recetaNombre,
        String usuarioCorreo,
        String notas,
        LocalDateTime cocinadoAt
) {
}
