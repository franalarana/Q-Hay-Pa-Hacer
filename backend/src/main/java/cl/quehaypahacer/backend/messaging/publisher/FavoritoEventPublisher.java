package cl.quehaypahacer.backend.messaging.publisher;

import cl.quehaypahacer.backend.messaging.event.FavoritoEvent;

/**
 * Puerto de salida para eventos de favoritos (Indicador 3).
 * <p>
 * Los servicios de negocio (FavoritoService) solo conocen esta interfaz
 * y desconocen la infraestructura de mensajería subyacente.
 */
public interface FavoritoEventPublisher {

    /**
     * Publica de forma asíncrona y tras el commit de BD un evento de favorito.
     *
     * @param event datos del cambio de favorito
     */
    void publicarFavorito(FavoritoEvent event);
}
