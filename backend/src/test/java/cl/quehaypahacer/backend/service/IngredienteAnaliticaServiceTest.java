package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.analitica.IngredienteRankingDTO;
import cl.quehaypahacer.backend.dto.analitica.ResumenAnaliticaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IngredienteAnaliticaServiceTest {

    private IngredienteAnaliticaService service;

    @BeforeEach
    void setUp() {
        service = new IngredienteAnaliticaService();
    }

    @Test
    void registrarYObtenerTopDespensa_ordenaPorFrecuenciaCorrectamente() {
        service.registrarIngredienteDespensa("Tomate");
        service.registrarIngredienteDespensa("Cebolla");
        service.registrarIngredienteDespensa("Tomate");
        service.registrarIngredienteDespensa("Arroz");
        service.registrarIngredienteDespensa("tomate"); // prueba normalización case-insensitive

        List<IngredienteRankingDTO> top = service.obtenerTopIngredientesDespensa(5);

        assertFalse(top.isEmpty());
        assertEquals("Tomate", top.get(0).nombreIngrediente());
        assertEquals(3, top.get(0).totalOcurrencias());
        assertTrue(top.get(0).porcentaje() > 50.0);
    }

    @Test
    void obtenerResumen_consolidaTotalesCorrectamente() {
        service.registrarIngredienteDespensa("Harina");
        service.registrarIngredienteCocina("Harina");
        service.registrarIngredienteCocina("Levadura");

        ResumenAnaliticaDTO resumen = service.obtenerResumen();

        assertEquals(3, resumen.totalEventosProcesados());
        assertEquals(1, resumen.totalEventosDespensa());
        assertEquals(2, resumen.totalEventosCocina());
    }
}
