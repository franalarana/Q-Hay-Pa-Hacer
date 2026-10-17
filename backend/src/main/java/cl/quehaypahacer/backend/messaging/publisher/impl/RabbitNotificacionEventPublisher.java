package cl.quehaypahacer.backend.messaging.publisher.impl;

import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import cl.quehaypahacer.backend.messaging.event.NotificacionDespensaEvent;
import cl.quehaypahacer.backend.messaging.publisher.NotificacionEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Adaptador RabbitMQ para publicación de notificaciones de despensa.
 * <p>
 * Publica en el exchange principal usando la routing key de notificaciones.
 * Se asegura de enviar el mensaje únicamente tras el commit exitoso de la transacción.
 * Si RabbitMQ está caído, registra el error y permite continuar el flujo HTTP normal.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitNotificacionEventPublisher implements NotificacionEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties props;

    @Override
    public void publicarNotificacionDespensa(NotificacionDespensaEvent event) {
        Runnable envio = () -> {
            try {
                rabbitTemplate.convertAndSend(
                        props.exchanges().principal(),
                        props.routingKeys().notificaciones(),
                        event);
                log.info("[PUBLISHER] NotificacionDespensaEvent enviado: itemDespensaId={}, accion={}",
                        event.ingredienteUsuarioId(), event.accion());
            } catch (AmqpException e) {
                log.error("[PUBLISHER] No se pudo publicar NotificacionDespensaEvent {}: {}", event, e.getMessage());
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    envio.run();
                }
            });
        } else {
            envio.run();
        }
    }
}
