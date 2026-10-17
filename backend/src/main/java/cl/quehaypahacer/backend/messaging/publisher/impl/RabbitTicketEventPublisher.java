package cl.quehaypahacer.backend.messaging.publisher.impl;

import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import cl.quehaypahacer.backend.messaging.event.TicketCocinaEvent;
import cl.quehaypahacer.backend.messaging.publisher.TicketEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Adaptador RabbitMQ para publicación de eventos de tickets de cocinado.
 * <p>
 * Publica en el exchange principal usando la routing key configurada.
 * Se asegura de enviar el mensaje únicamente después de que la transacción
 * de base de datos haya realizado commit (evitando mensajes huérfanos).
 * Si RabbitMQ está caído o inaccesible, captura la excepción y registra
 * un error sin interrumpir el flujo del usuario.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitTicketEventPublisher implements TicketEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties props;

    @Override
    public void publicarTicketCocina(TicketCocinaEvent event) {
        Runnable envio = () -> {
            try {
                rabbitTemplate.convertAndSend(
                        props.exchanges().principal(),
                        props.routingKeys().tickets(),
                        event);
                log.info("[PUBLISHER] TicketCocinaEvent enviado: historialId={}", event.historialId());
            } catch (AmqpException e) {
                log.error("[PUBLISHER] No se pudo publicar TicketCocinaEvent {}: {}", event, e.getMessage());
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
