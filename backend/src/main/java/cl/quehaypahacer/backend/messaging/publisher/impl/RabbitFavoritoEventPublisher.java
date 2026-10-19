package cl.quehaypahacer.backend.messaging.publisher.impl;

import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import cl.quehaypahacer.backend.messaging.event.FavoritoEvent;
import cl.quehaypahacer.backend.messaging.publisher.FavoritoEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Adaptador RabbitMQ para publicación de eventos de favoritos.
 * <p>
 * Publica en el exchange principal usando la routing key de favoritos.
 * Se asegura de enviar el mensaje únicamente después del commit de base de datos.
 * Si RabbitMQ está inaccesible, captura la excepción y registra un error sin fallar la petición.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitFavoritoEventPublisher implements FavoritoEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties props;

    @Override
    public void publicarFavorito(FavoritoEvent event) {
        Runnable envio = () -> {
            try {
                rabbitTemplate.convertAndSend(
                        props.exchanges().principal(),
                        props.routingKeys().favoritos(),
                        event);
                log.info("[PUBLISHER] FavoritoEvent enviado: recetaId={}, accion={}", event.recetaId(), event.accion());
            } catch (AmqpException e) {
                log.error("[PUBLISHER] No se pudo publicar FavoritoEvent {}: {}", event, e.getMessage());
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
