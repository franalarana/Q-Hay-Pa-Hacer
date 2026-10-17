package cl.quehaypahacer.backend.messaging.consumer.ticket;

import cl.quehaypahacer.backend.messaging.event.TicketCocinaEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor asíncrono para generar fichas/tickets de cocinado (Indicador 4 y 5).
 * <p>
 * Implementa ACK manual:
 * <ul>
 *     <li>Si el evento es válido, procesa la ficha y envía {@code basicAck}.</li>
 *     <li>Si el evento viola las reglas de negocio (p. ej. {@code recetaId <= 0}),
 *     lo rechaza con {@code basicNack(tag, false, false)} para enviarlo al Dead Letter Exchange.</li>
 * </ul>
 */
@Slf4j
@Component
public class TicketCocinaConsumer {

    @RabbitListener(queues = "${app.rabbitmq.queues.tickets}", ackMode = "MANUAL")
    public void onTicketCocina(TicketCocinaEvent event,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            validar(event);
            generarTicket(event);
            channel.basicAck(deliveryTag, false);
            log.info("[ACK] Ticket generado historialId={} receta='{}'", event.historialId(), event.recetaNombre());
        } catch (Exception e) {
            log.error("[NACK → DLQ] TicketCocinaEvent rechazado: {} | motivo: {}", event, e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * Valida el evento según el contrato acordado.
     * Un evento es inválido si {@code recetaId == null || recetaId <= 0}.
     */
    private void validar(TicketCocinaEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("El evento TicketCocinaEvent no puede ser nulo");
        }
        if (event.recetaId() == null || event.recetaId() <= 0) {
            throw new IllegalArgumentException("recetaId es inválido (debe ser mayor a 0): " + event.recetaId());
        }
    }

    /**
     * Simulación de generación del ticket / ficha de cocinado.
     */
    private void generarTicket(TicketCocinaEvent event) {
        log.info("""
                =================== FICHA DE COCINADO ===================
                Historial ID : {}
                Receta ID    : {}
                Receta       : {}
                Usuario      : {}
                Notas        : {}
                Fecha/Hora   : {}
                =========================================================""",
                event.historialId(),
                event.recetaId(),
                event.recetaNombre(),
                event.usuarioCorreo(),
                event.notas() != null ? event.notas() : "(sin notas)",
                event.cocinadoAt());
    }
}
