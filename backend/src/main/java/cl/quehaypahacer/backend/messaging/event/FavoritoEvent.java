package cl.quehaypahacer.backend.messaging.event;

import java.time.LocalDateTime;

/**
 * Evento publicado cuando un usuario marca o desmarca una receta como favorita.
 * Lo consume FavoritoConsumer para procesar la acción de forma asíncrona.
 *
 * @param favoritoId   id del registro o null si fue desmarcado
 * @param recetaId     id de la receta
 * @param recetaTitulo título de la receta
 * @param usuarioCorreo correo del usuario
 * @param accion       tipo de cambio (AGREGADO o ELIMINADO)
 * @param ocurridoAt   marca temporal del evento
 */
public record FavoritoEvent(
        Long favoritoId,
        Long recetaId,
        String recetaTitulo,
        String usuarioCorreo,
        Accion accion,
        LocalDateTime ocurridoAt
) {

    /** Tipo de cambio realizado en favoritos. */
    public enum Accion {
        AGREGADO,
        ELIMINADO
    }
}
