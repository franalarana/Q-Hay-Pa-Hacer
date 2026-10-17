package cl.quehaypahacer.backend.messaging.publisher;

import cl.quehaypahacer.backend.messaging.event.TicketCocinaEvent;

/**
 * Puerto de salida para eventos de tickets de cocinado (Indicador 3).
 * <p>
 * Los servicios de negocio (HistorialCocinaService) solo conocen esta interfaz
 * y desconocen la infraestructura de mensajería (RabbitMQ, brokers, etc.).
 */
public interface TicketEventPublisher {

    /**
     * Publica de forma asíncrona y tras el commit de BD un evento de ticket de cocinado.
     *
     * @param event datos del ticket de cocinado
     */
    void publicarTicketCocina(TicketCocinaEvent event);
}
