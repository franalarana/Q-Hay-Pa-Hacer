package cl.quehaypahacer.backend.messaging.consumer.dlq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Consumidor de auditoría para las Dead Letter Queues (DLQ) (Indicador 5).
 * <p>
 * Escucha ambas colas DLQ (tickets y notificaciones) e inspecciona los mensajes
 * que fueron rechazados con {@code basicNack} o fallaron en deserialización.
 * Registra un log de advertencia con la cola consumida, payload y cabeceras {@code x-death},
 * y confirma con {@code basicAck} para desocupar la DLQ.
 * <p>
 * Se puede deshabilitar mediante {@code app.rabbitmq.dlq-auditor-enabled=false}
 * si se desea que los mensajes permanezcan visibles en RabbitMQ durante la evaluación.
 */
@Slf4j
@Component
public class DeadLetterQueueConsumer {

    @RabbitListener(
            queues = {"${app.rabbitmq.queues.tickets-dlq}", "${app.rabbitmq.queues.notificaciones-dlq}"},
            ackMode = "MANUAL",
            autoStartup = "${app.rabbitmq.dlq-auditor-enabled:true}"
    )
    public void auditar(Message message,
                        Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            String cola = (message.getMessageProperties() != null && message.getMessageProperties().getConsumerQueue() != null)
                    ? message.getMessageProperties().getConsumerQueue()
                    : "desconocida";
            Object xDeath = message.getMessageProperties() != null
                    ? message.getMessageProperties().getHeader("x-death")
                    : null;
            String body = message.getBody() != null
                    ? new String(message.getBody(), StandardCharsets.UTF_8)
                    : "(vacio)";

            log.warn("[DLQ AUDIT] Mensaje en DLQ detectado | Cola: '{}' | x-death: {} | Body: {}",
                    cola, xDeath, body);
        } catch (Exception e) {
            log.error("[DLQ AUDIT] Error al procesar mensaje en auditoría DLQ: {}", e.getMessage(), e);
        } finally {
            channel.basicAck(tag, false);
        }
    }
}
