package cl.quehaypahacer.backend.admin.rabbitmq.service.impl;

import cl.quehaypahacer.backend.admin.rabbitmq.dto.QueueRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.QueueStatusDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.exception.RabbitResourceNotFoundException;
import cl.quehaypahacer.backend.admin.rabbitmq.exception.RabbitResourceProtectedException;
import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RabbitAdminServiceImplTest {

    private RabbitAdmin rabbitAdmin;
    private RabbitAdminServiceImpl service;

    @BeforeEach
    void setUp() {
        rabbitAdmin = mock(RabbitAdmin.class);
        RabbitMQProperties props = new RabbitMQProperties(
                new RabbitMQProperties.Exchanges("recetas.exchange", "recetas.dlx.exchange"),
                new RabbitMQProperties.Queues("recetas.tickets.queue", "recetas.notificaciones.queue",
                        "recetas.tickets.dlq", "recetas.notificaciones.dlq"),
                new RabbitMQProperties.RoutingKeys("a", "b", "c", "d"));
        service = new RabbitAdminServiceImpl(rabbitAdmin, props);
    }

    @Test
    void crearColaDeclaraColaDurableConDlx() {
        service.crearCola(new QueueRequestDTO("mi.cola", true, false, "dlx", "dlq.key"));

        ArgumentCaptor<Queue> captor = ArgumentCaptor.forClass(Queue.class);
        verify(rabbitAdmin).declareQueue(captor.capture());
        Queue q = captor.getValue();
        assertEquals("mi.cola", q.getName());
        assertTrue(q.isDurable());
        assertEquals("dlx", q.getArguments().get("x-dead-letter-exchange"));
        assertEquals("dlq.key", q.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void eliminarColaInexistenteLanza404() {
        when(rabbitAdmin.getQueueInfo("x")).thenReturn(null);
        assertThrows(RabbitResourceNotFoundException.class, () -> service.eliminarCola("x"));
    }

    @Test
    void crearBindingConColaInexistenteLanzaNotFound() {
        when(rabbitAdmin.getQueueInfo("x")).thenReturn(null);
        assertThrows(RabbitResourceNotFoundException.class,
                () -> service.crearBinding(new cl.quehaypahacer.backend.admin.rabbitmq.dto.BindingRequestDTO("x", "e", "k")));
    }

    @Test
    void eliminarColaExistenteLaBorra() {
        when(rabbitAdmin.getQueueInfo("q")).thenReturn(new QueueInformation("q", 0, 0));
        service.eliminarCola("q");
        verify(rabbitAdmin).deleteQueue("q");
    }

    @Test
    void eliminarColaDelSistemaLanzaProtegida() {
        assertThrows(RabbitResourceProtectedException.class,
                () -> service.eliminarCola("recetas.tickets.dlq"));
    }

    @Test
    void eliminarExchangeDelSistemaLanzaProtegida() {
        assertThrows(RabbitResourceProtectedException.class,
                () -> service.eliminarExchange("recetas.exchange"));
    }

    @Test
    void estadoDeColaInexistenteLanzaNotFound() {
        when(rabbitAdmin.getQueueInfo("x")).thenReturn(null);
        assertThrows(RabbitResourceNotFoundException.class, () -> service.obtenerEstadoCola("x"));
    }

    @Test
    void estadoDeColaDevuelveConteos() {
        when(rabbitAdmin.getQueueInfo("q")).thenReturn(new QueueInformation("q", 5, 2));
        QueueStatusDTO dto = service.obtenerEstadoCola("q");
        assertEquals(5, dto.messageCount());
        assertEquals(2, dto.consumerCount());
    }
}
