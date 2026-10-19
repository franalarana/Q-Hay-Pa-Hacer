package cl.quehaypahacer.backend.messaging.publisher.impl;

import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import cl.quehaypahacer.backend.messaging.event.FavoritoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitFavoritoEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMQProperties props;
    private RabbitFavoritoEventPublisher publisher;

    @BeforeEach
    void setUp() {
        props = new RabbitMQProperties(
                new RabbitMQProperties.Exchanges("recetas.exchange", "recetas.dlx.exchange"),
                new RabbitMQProperties.Queues("recetas.tickets.queue", "recetas.notificaciones.queue", "recetas.favoritos.queue",
                        "recetas.tickets.dlq", "recetas.notificaciones.dlq", "recetas.favoritos.dlq"),
                new RabbitMQProperties.RoutingKeys("recetas.ticket.generar", "recetas.despensa.notificar", "recetas.favorito.marcar",
                        "recetas.ticket.dlq", "recetas.despensa.dlq", "recetas.favorito.dlq")
        );
        publisher = new RabbitFavoritoEventPublisher(rabbitTemplate, props);
    }

    @Test
    void publicarFavorito_enviaMensajeConExchangeYRoutingKey() {
        FavoritoEvent event = new FavoritoEvent(
                1L, 10L, "Pastel de Choclo", "usuario@test.cl",
                FavoritoEvent.Accion.AGREGADO, LocalDateTime.now()
        );

        publisher.publicarFavorito(event);

        verify(rabbitTemplate).convertAndSend(
                eq("recetas.exchange"),
                eq("recetas.favorito.marcar"),
                eq(event)
        );
    }

    @Test
    void publicarFavorito_siRabbitFallaNoLanzaExcepcion() {
        FavoritoEvent event = new FavoritoEvent(
                1L, 10L, "Pastel de Choclo", "usuario@test.cl",
                FavoritoEvent.Accion.AGREGADO, LocalDateTime.now()
        );

        doThrow(new AmqpConnectException(new RuntimeException("Broker caído")))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(Object.class));

        assertDoesNotThrow(() -> publisher.publicarFavorito(event));
    }
}
