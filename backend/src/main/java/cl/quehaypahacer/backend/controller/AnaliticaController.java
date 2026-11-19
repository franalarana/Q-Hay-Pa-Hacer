package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.dto.analitica.IngredienteRankingDTO;
import cl.quehaypahacer.backend.dto.analitica.ResumenAnaliticaDTO;
import cl.quehaypahacer.backend.service.IngredienteAnaliticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para consultar analítica y métricas generadas en tiempo real mediante Apache Kafka.
 */
@RestController
@RequestMapping("/api/analitica")
@RequiredArgsConstructor
public class AnaliticaController {

    private final IngredienteAnaliticaService analiticaService;

    @GetMapping("/ingredientes/mas-comunes")
    public ResponseEntity<List<IngredienteRankingDTO>> obtenerIngredientesMasComunes(
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(analiticaService.obtenerTopIngredientesDespensa(limite));
    }

    @GetMapping("/ingredientes/mas-cocinados")
    public ResponseEntity<List<IngredienteRankingDTO>> obtenerIngredientesMasCocinados(
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(analiticaService.obtenerTopIngredientesCocina(limite));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenAnaliticaDTO> obtenerResumenAnalitica() {
        return ResponseEntity.ok(analiticaService.obtenerResumen());
    }
}
