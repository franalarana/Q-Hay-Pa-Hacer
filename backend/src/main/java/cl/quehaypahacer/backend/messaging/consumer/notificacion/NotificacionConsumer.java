package cl.quehaypahacer.backend.messaging.consumer.notificacion;

import cl.quehaypahacer.backend.messaging.event.NotificacionDespensaEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor asíncrono para notificaciones de cambios en la despensa (Indicador 4 y 5).
 * <p>
 * Implementa ACK manual:
 * <ul>
 *     <li>Si el evento es válido, emite el aviso y confirma con {@code basicAck}.</li>
 *     <li>Si el evento es inválido (p. ej. {@code ingredienteUsuarioId <= 0} o {@code accion == null}),
 *     lo rechaza con {@code basicNack(tag, false, false)} para enviarlo a la DLQ.</li>
 * </ul>
 */
@Slf4j
@Component
public class NotificacionConsumer {

    @RabbitListener(queues = "${app.rabbitmq.queues.notificaciones}", ackMode = "MANUAL")
    public void onNotificacionDespensa(NotificacionDespensaEvent event,
                                       Channel channel,
                                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            validar(event);
            simularAviso(event);
            channel.basicAck(deliveryTag, false);
            log.info("[ACK] Notificación despensa procesada: ingredienteUsuarioId={} accion={}",
                    event.ingredienteUsuarioId(), event.accion());
        } catch (Exception e) {
            log.error("[NACK → DLQ] NotificacionDespensaEvent rechazado: {} | motivo: {}", event, e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * Valida el evento según el contrato acordado:
     * {@code ingredienteUsuarioId == null || ingredienteUsuarioId <= 0 || accion == null}.
     */
    private void validar(NotificacionDespensaEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("El evento NotificacionDespensaEvent no puede ser nulo");
        }
        if (event.ingredienteUsuarioId() == null || event.ingredienteUsuarioId() <= 0) {
            throw new IllegalArgumentException("ingredienteUsuarioId inválido (debe ser mayor a 0): " + event.ingredienteUsuarioId());
        }
        if (event.accion() == null) {
            throw new IllegalArgumentException("La acción de la despensa no puede ser nula");
        }
    }

    /**
     * Simulación del envío de aviso al usuario.
     * Ejemplo: "🔔 [usuario] tu despensa: Tomate AGREGADO (3 UNIDAD)"
     */
    private void simularAviso(NotificacionDespensaEvent event) {
        String detalleCantidad = event.cantidad() != null
                ? (event.cantidad() + (event.unidad() != null ? " " + event.unidad() : ""))
                : "(eliminado)";

        log.info("🔔 [{}] tu despensa: {} {} ({})",
                event.usuarioCorreo(),
                event.ingredienteNombre(),
                event.accion(),
                detalleCantidad);
    }
}
