package cl.quehaypahacer.backend.messaging.publisher;

import cl.quehaypahacer.backend.messaging.event.NotificacionDespensaEvent;

/**
 * Puerto de salida para eventos de cambios en la despensa (Indicador 3).
 * <p>
 * Los servicios de negocio (DespensaService) solo conocen esta interfaz
 * y desconocen la infraestructura de mensajería subyacente.
 */
public interface NotificacionEventPublisher {

    /**
     * Publica de forma asíncrona y tras el commit de BD un evento de despensa.
     *
     * @param event datos del cambio de despensa
     */
    void publicarNotificacionDespensa(NotificacionDespensaEvent event);
}
