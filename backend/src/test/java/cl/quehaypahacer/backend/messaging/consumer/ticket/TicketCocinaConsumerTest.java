package cl.quehaypahacer.backend.messaging.consumer.ticket;

import cl.quehaypahacer.backend.messaging.event.TicketCocinaEvent;
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
class TicketCocinaConsumerTest {

    @Mock
    private Channel channel;

    private TicketCocinaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new TicketCocinaConsumer();
    }

    @Test
    void onTicketCocina_conEventoValido_realizaBasicAck() throws IOException {
        TicketCocinaEvent event = new TicketCocinaEvent(
                1L, 10L, "Pastel de Choclo", "usuario@test.cl", "Sin pasas", LocalDateTime.now()
        );
        long deliveryTag = 123L;

        consumer.onTicketCocina(event, channel, deliveryTag);

        verify(channel).basicAck(deliveryTag, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void onTicketCocina_conRecetaIdNegativo_realizaBasicNackSinRequeue() throws IOException {
        TicketCocinaEvent event = new TicketCocinaEvent(
                1L, -1L, "Receta Invalida", "usuario@test.cl", null, LocalDateTime.now()
        );
        long deliveryTag = 124L;

        consumer.onTicketCocina(event, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void onTicketCocina_conRecetaIdCero_realizaBasicNackSinRequeue() throws IOException {
        TicketCocinaEvent event = new TicketCocinaEvent(
                1L, 0L, "Receta Invalida", "usuario@test.cl", null, LocalDateTime.now()
        );
        long deliveryTag = 125L;

        consumer.onTicketCocina(event, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void onTicketCocina_conRecetaIdNull_realizaBasicNackSinRequeue() throws IOException {
        TicketCocinaEvent event = new TicketCocinaEvent(
                1L, null, "Receta Invalida", "usuario@test.cl", null, LocalDateTime.now()
        );
        long deliveryTag = 126L;

        consumer.onTicketCocina(event, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void onTicketCocina_conEventoNull_realizaBasicNackSinRequeue() throws IOException {
        long deliveryTag = 127L;

        consumer.onTicketCocina(null, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}
