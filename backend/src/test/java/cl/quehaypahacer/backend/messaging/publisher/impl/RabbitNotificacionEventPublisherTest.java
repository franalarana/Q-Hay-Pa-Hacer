package cl.quehaypahacer.backend.messaging.publisher.impl;

import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import cl.quehaypahacer.backend.messaging.event.NotificacionDespensaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitNotificacionEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMQProperties props;
    private RabbitNotificacionEventPublisher publisher;

    @BeforeEach
    void setUp() {
        props = new RabbitMQProperties(
                new RabbitMQProperties.Exchanges("recetas.exchange", "recetas.dlx.exchange"),
                new RabbitMQProperties.Queues("recetas.tickets.queue", "recetas.notificaciones.queue", "recetas.favoritos.queue",
                        "recetas.tickets.dlq", "recetas.notificaciones.dlq", "recetas.favoritos.dlq"),
                new RabbitMQProperties.RoutingKeys("recetas.ticket.generar", "recetas.despensa.notificar", "recetas.favorito.marcar",
                        "recetas.ticket.dlq", "recetas.despensa.dlq", "recetas.favorito.dlq")
        );
        publisher = new RabbitNotificacionEventPublisher(rabbitTemplate, props);
    }

    @Test
    void publicarNotificacionDespensa_enviaMensajeConExchangeYRoutingKey() {
        NotificacionDespensaEvent event = new NotificacionDespensaEvent(
                5L, "Harina", 1.0, "KILOGRAMO", NotificacionDespensaEvent.Accion.AGREGADO,
                "usuario@test.cl", LocalDateTime.now()
        );

        publisher.publicarNotificacionDespensa(event);

        verify(rabbitTemplate).convertAndSend(
                eq("recetas.exchange"),
                eq("recetas.despensa.notificar"),
                eq(event)
        );
    }

    @Test
    void publicarNotificacionDespensa_siRabbitFallaNoLanzaExcepcion() {
        NotificacionDespensaEvent event = new NotificacionDespensaEvent(
                5L, "Harina", 1.0, "KILOGRAMO", NotificacionDespensaEvent.Accion.AGREGADO,
                "usuario@test.cl", LocalDateTime.now()
        );

        doThrow(new AmqpConnectException(new RuntimeException("Broker caído")))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(Object.class));

        assertDoesNotThrow(() -> publisher.publicarNotificacionDespensa(event));
    }
}
