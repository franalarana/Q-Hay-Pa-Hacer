package cl.quehaypahacer.backend.dto.analitica;

import java.util.List;

/**
 * Resumen consolidado del procesamiento de analítica y streaming en tiempo real.
 */
public record ResumenAnaliticaDTO(
        long totalEventosProcesados,
        long totalEventosDespensa,
        long totalEventosCocina,
        List<IngredienteRankingDTO> topDespensa,
        List<IngredienteRankingDTO> topCocina
) {}
