package cl.quehaypahacer.backend.dto.analitica;

/**
 * DTO para representar el ranking de un ingrediente según frecuencia en eventos de streaming.
 */
public record IngredienteRankingDTO(
        String nombreIngrediente,
        long totalOcurrencias,
        double porcentaje
) {}
