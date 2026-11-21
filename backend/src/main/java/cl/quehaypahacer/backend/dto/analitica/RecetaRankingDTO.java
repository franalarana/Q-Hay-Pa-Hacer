package cl.quehaypahacer.backend.dto.analitica;

/**
 * DTO para representar el ranking de recetas más cocinadas (top semanal).
 */
public record RecetaRankingDTO(
        Long recetaId,
        String titulo,
        Long totalCocinadas,
        Double porcentaje
) {}
