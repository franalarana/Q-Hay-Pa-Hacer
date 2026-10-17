package cl.quehaypahacer.backend.messaging.consumer.dlq;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeadLetterQueueConsumerTest {

    @Mock
    private Channel channel;

    private DeadLetterQueueConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new DeadLetterQueueConsumer();
    }

    @Test
    void auditar_conMensajeCompleto_registraYRealizaBasicAck() throws IOException {
        MessageProperties properties = new MessageProperties();
        properties.setConsumerQueue("recetas.tickets.dlq");
        properties.setReceivedRoutingKey("recetas.ticket.dlq");
        properties.setHeader("x-death", List.of(Map.of("reason", "rejected", "queue", "recetas.tickets.queue", "count", 1L)));

        byte[] body = "{\"recetaId\": -1}".getBytes(StandardCharsets.UTF_8);
        Message message = new Message(body, properties);
        long deliveryTag = 301L;

        consumer.auditar(message, channel, deliveryTag);

        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    void auditar_conMensajeSinHeadersNiBody_realizaBasicAck() throws IOException {
        Message message = new Message(new byte[0], new MessageProperties());
        long deliveryTag = 302L;

        consumer.auditar(message, channel, deliveryTag);

        verify(channel).basicAck(deliveryTag, false);
    }
}
