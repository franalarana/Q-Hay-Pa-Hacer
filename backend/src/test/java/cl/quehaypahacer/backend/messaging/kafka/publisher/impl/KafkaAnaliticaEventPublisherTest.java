package cl.quehaypahacer.backend.messaging.kafka.publisher.impl;

import cl.quehaypahacer.backend.config.kafka.KafkaProperties;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteCocinaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteDespensaAnaliticaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaAnaliticaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaProperties props;
    private SpringKafkaAnaliticaEventPublisher publisher;

    @BeforeEach
    void setUp() {
        props = new KafkaProperties(
                new KafkaProperties.Topics("analitica.despensa.ingredientes", "analitica.cocina.ingredientes"),
                2,
                (short) 1,
                604800000L,
                "delete",
                new KafkaProperties.ConsumerGroups("quehaypahacer-analitica-group", "quehaypahacer-auditoria-group")
        );
        publisher = new SpringKafkaAnaliticaEventPublisher(kafkaTemplate, props);
    }

    @Test
    void publicarEventoDespensa_enviaMensajeConTopicoYClave() {
        IngredienteDespensaAnaliticaEvent event = new IngredienteDespensaAnaliticaEvent(
                1L, "Cebolla", 2.0, "UNIDAD", IngredienteDespensaAnaliticaEvent.Accion.AGREGADO,
                "user@test.cl", LocalDateTime.now()
        );

        when(kafkaTemplate.send(any(), any(), any())).thenReturn(new CompletableFuture<>());

        publisher.publicarEventoDespensa(event);

        verify(kafkaTemplate).send(
                eq("analitica.despensa.ingredientes"),
                eq("Cebolla"),
                eq(event)
        );
    }

    @Test
    void publicarEventoCocina_enviaMensajeConTopicoYClave() {
        IngredienteCocinaAnaliticaEvent event = new IngredienteCocinaAnaliticaEvent(
                5L, "Empanadas", 2L, "Carne Picada", 500.0, "GRAMO",
                "user@test.cl", LocalDateTime.now()
        );

        when(kafkaTemplate.send(any(), any(), any())).thenReturn(new CompletableFuture<>());

        publisher.publicarEventoCocina(event);

        verify(kafkaTemplate).send(
                eq("analitica.cocina.ingredientes"),
                eq("Carne Picada"),
                eq(event)
        );
    }
}
