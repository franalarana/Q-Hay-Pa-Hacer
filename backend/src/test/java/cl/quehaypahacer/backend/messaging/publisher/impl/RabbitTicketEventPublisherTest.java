package cl.quehaypahacer.backend.messaging.publisher.impl;

import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import cl.quehaypahacer.backend.messaging.event.TicketCocinaEvent;
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
class RabbitTicketEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMQProperties props;
    private RabbitTicketEventPublisher publisher;

    @BeforeEach
    void setUp() {
        props = new RabbitMQProperties(
                new RabbitMQProperties.Exchanges("recetas.exchange", "recetas.dlx.exchange"),
                new RabbitMQProperties.Queues("recetas.tickets.queue", "recetas.notificaciones.queue",
                        "recetas.tickets.dlq", "recetas.notificaciones.dlq"),
                new RabbitMQProperties.RoutingKeys("recetas.ticket.generar", "recetas.despensa.notificar",
                        "recetas.ticket.dlq", "recetas.despensa.dlq")
        );
        publisher = new RabbitTicketEventPublisher(rabbitTemplate, props);
    }

    @Test
    void publicarTicketCocina_enviaMensajeConExchangeYRoutingKey() {
        TicketCocinaEvent event = new TicketCocinaEvent(
                1L, 10L, "Pastel de Choclo", "usuario@test.cl", "Sin pasas", LocalDateTime.now()
        );

        publisher.publicarTicketCocina(event);

        verify(rabbitTemplate).convertAndSend(
                eq("recetas.exchange"),
                eq("recetas.ticket.generar"),
                eq(event)
        );
    }

    @Test
    void publicarTicketCocina_siRabbitFallaNoLanzaExcepcion() {
        TicketCocinaEvent event = new TicketCocinaEvent(
                1L, 10L, "Pastel de Choclo", "usuario@test.cl", "Sin pasas", LocalDateTime.now()
        );

        doThrow(new AmqpConnectException(new RuntimeException("Broker caído")))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(Object.class));

        assertDoesNotThrow(() -> publisher.publicarTicketCocina(event));
    }
}
