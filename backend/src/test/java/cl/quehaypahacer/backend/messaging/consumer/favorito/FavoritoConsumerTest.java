package cl.quehaypahacer.backend.messaging.consumer.favorito;

import cl.quehaypahacer.backend.messaging.event.FavoritoEvent;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FavoritoConsumerTest {

    @Mock
    private Channel channel;

    private FavoritoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new FavoritoConsumer();
    }

    @Test
    void onFavorito_conEventoValido_realizaBasicAck() throws IOException {
        FavoritoEvent event = new FavoritoEvent(
                1L, 10L, "Pastel de Choclo", "usuario@test.cl",
                FavoritoEvent.Accion.AGREGADO, LocalDateTime.now()
        );
        long deliveryTag = 100L;

        consumer.onFavorito(event, channel, deliveryTag);

        verify(channel).basicAck(deliveryTag, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void onFavorito_conRecetaIdNegativo_realizaBasicNackSinRequeue() throws IOException {
        FavoritoEvent event = new FavoritoEvent(
                1L, -1L, "Receta Invalida", "usuario@test.cl",
                FavoritoEvent.Accion.AGREGADO, LocalDateTime.now()
        );
        long deliveryTag = 101L;

        consumer.onFavorito(event, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void onFavorito_conRecetaIdNull_realizaBasicNackSinRequeue() throws IOException {
        FavoritoEvent event = new FavoritoEvent(
                1L, null, "Receta Invalida", "usuario@test.cl",
                FavoritoEvent.Accion.AGREGADO, LocalDateTime.now()
        );
        long deliveryTag = 102L;

        consumer.onFavorito(event, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void onFavorito_conAccionNull_realizaBasicNackSinRequeue() throws IOException {
        FavoritoEvent event = new FavoritoEvent(
                1L, 5L, "Receta Sin Accion", "usuario@test.cl",
                null, LocalDateTime.now()
        );
        long deliveryTag = 103L;

        consumer.onFavorito(event, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void onFavorito_conEventoNull_realizaBasicNackSinRequeue() throws IOException {
        long deliveryTag = 104L;

        consumer.onFavorito(null, channel, deliveryTag);

        verify(channel).basicNack(deliveryTag, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}
