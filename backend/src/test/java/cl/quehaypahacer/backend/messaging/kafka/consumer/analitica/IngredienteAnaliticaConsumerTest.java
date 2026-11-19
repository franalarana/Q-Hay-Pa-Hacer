package cl.quehaypahacer.backend.messaging.kafka.consumer.analitica;

import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteCocinaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteDespensaAnaliticaEvent;
import cl.quehaypahacer.backend.service.IngredienteAnaliticaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredienteAnaliticaConsumerTest {

    @Mock
    private IngredienteAnaliticaService analiticaService;

    @InjectMocks
    private IngredienteAnaliticaConsumer consumer;

    @Test
    void consumirEventoDespensa_agregaAlServicioSiEsValido() {
        IngredienteDespensaAnaliticaEvent event = new IngredienteDespensaAnaliticaEvent(
                1L, "Papas", 3.0, "KILOGRAMO", IngredienteDespensaAnaliticaEvent.Accion.AGREGADO,
                "test@correo.cl", LocalDateTime.now()
        );

        consumer.consumirEventoDespensa(event, 0, 10L);

        verify(analiticaService).registrarIngredienteDespensa("Papas");
    }

    @Test
    void consumirEventoDespensa_noAgregaSiAccionEsEliminado() {
        IngredienteDespensaAnaliticaEvent event = new IngredienteDespensaAnaliticaEvent(
                1L, "Papas", 0.0, "KILOGRAMO", IngredienteDespensaAnaliticaEvent.Accion.ELIMINADO,
                "test@correo.cl", LocalDateTime.now()
        );

        consumer.consumirEventoDespensa(event, 0, 11L);

        verify(analiticaService, never()).registrarIngredienteDespensa(any());
    }

    @Test
    void consumirEventoCocina_agregaAlServicioSiEsValido() {
        IngredienteCocinaAnaliticaEvent event = new IngredienteCocinaAnaliticaEvent(
                10L, "Puré con Huevo", 1L, "Papas", 1000.0, "GRAMO",
                "test@correo.cl", LocalDateTime.now()
        );

        consumer.consumirEventoCocina(event, 1, 42L);

        verify(analiticaService).registrarIngredienteCocina("Papas");
    }
}
