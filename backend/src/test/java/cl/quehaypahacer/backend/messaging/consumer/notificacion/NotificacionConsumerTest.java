package cl.quehaypahacer.backend.messaging.consumer.notificacion;

import cl.quehaypahacer.backend.messaging.event.NotificacionDespensaEvent;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionConsumerTest {

    @Mock
    private Channel channel;

    private NotificacionConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new NotificacionConsumer();
    }

    @Test
    void onNotificacionDespensa_conEventoValido_realizaBasicAck() throws IOException {
        NotificacionDespensaEvent event = new NotificacionDespensaEvent(
                1L, "Tomate", 3.0, "UNIDAD",
                NotificacionDespensaEvent.Accion.AGREGADO, "usuario@test.cl", LocalDateTime.now()
        );
        long deliveryTag = 201L;

        consumer.onNotificacionDespensa(event, channel, deliveryTag);

        verify(channel).basicAck(deliveryTag, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void onNotificacionDespensa_conItemEliminado_realizaBasicAck() throws IOException {
        NotificacionDespensaEvent event = new NotificacionDespensaEvent(
                2L, "Cebolla", null, null,
                NotificacionDespensaEvent.Accion.ELIMINADO, "usuario@test.cl", LocalDateTime.now()
        );
        long deliveryTag = 202L;

        consumer.onNotificacionDespensa(event, channel, deliveryTag);

        verify(channel).basicAck(deliveryTag, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void onNotificacionDespensa_conIngredienteUsuarioIdNegativo_realizaBasicNackSinRequeue() throws IOException {
        NotificacionDespensaEvent event = new NotificacionDespensaEvent(
                -1L, "Sal", 1.0, "KILOGRAMO",
                NotificacionDespensaEvent.Accion.AGREGADO, "usuario@test.cl", LocalDateTime.now()
        );
        long deliveryTag = 203L;

        consumer.onNotificacionDespensa(event, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void onNotificacionDespensa_conIngredienteUsuarioIdNull_realizaBasicNackSinRequeue() throws IOException {
        NotificacionDespensaEvent event = new NotificacionDespensaEvent(
                null, "Sal", 1.0, "KILOGRAMO",
                NotificacionDespensaEvent.Accion.AGREGADO, "usuario@test.cl", LocalDateTime.now()
        );
        long deliveryTag = 204L;

        consumer.onNotificacionDespensa(event, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void onNotificacionDespensa_conAccionNull_realizaBasicNackSinRequeue() throws IOException {
        NotificacionDespensaEvent event = new NotificacionDespensaEvent(
                5L, "Pimienta", 1.0, "GRAMO",
                null, "usuario@test.cl", LocalDateTime.now()
        );
        long deliveryTag = 205L;

        consumer.onNotificacionDespensa(event, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void onNotificacionDespensa_conEventoNull_realizaBasicNackSinRequeue() throws IOException {
        long deliveryTag = 206L;

        consumer.onNotificacionDespensa(null, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}
