package cl.quehaypahacer.backend.messaging.consumer.favorito;

import cl.quehaypahacer.backend.messaging.event.FavoritoEvent;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor asíncrono para eventos de recetas favoritas (Indicador 4 y 5).
 * <p>
 * Implementa ACK manual:
 * <ul>
 *     <li>Si el evento es válido, registra el aviso y confirma con {@code basicAck}.</li>
 *     <li>Si el evento es inválido (p. ej. {@code recetaId <= 0} o {@code accion == null}),
 *     lo rechaza con {@code basicNack(tag, false, false)} para enviarlo a su DLQ.</li>
 * </ul>
 */
@Slf4j
@Component
public class FavoritoConsumer {

    @RabbitListener(queues = "${app.rabbitmq.queues.favoritos}", ackMode = "MANUAL")
    public void onFavorito(FavoritoEvent event,
                           Channel channel,
                           @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            validar(event);
            procesarFavorito(event);
            channel.basicAck(deliveryTag, false);
            log.info("[ACK] Favorito procesado recetaId={} accion={}", event.recetaId(), event.accion());
        } catch (Exception e) {
            log.error("[NACK → DLQ] FavoritoEvent rechazado: {} | motivo: {}", event, e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * Valida el evento según el contrato: recetaId > 0 y acción no nula.
     */
    private void validar(FavoritoEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("El evento FavoritoEvent no puede ser nulo");
        }
        if (event.recetaId() == null || event.recetaId() <= 0) {
            throw new IllegalArgumentException("recetaId es inválido (debe ser mayor a 0): " + event.recetaId());
        }
        if (event.accion() == null) {
            throw new IllegalArgumentException("La acción de favorito no puede ser nula");
        }
    }

    /**
     * Simulación de procesamiento en segundo plano (p. ej. métricas de popularidad).
     */
    private void procesarFavorito(FavoritoEvent event) {
        log.info("⭐ [{}] receta '{}' (ID: {}) marcada como {}",
                event.usuarioCorreo(),
                event.recetaTitulo(),
                event.recetaId(),
                event.accion());
    }
}
