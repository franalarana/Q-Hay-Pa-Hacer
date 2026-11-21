package cl.quehaypahacer.backend.repository;

import cl.quehaypahacer.backend.dto.analitica.RecetaRankingDTO;
import cl.quehaypahacer.backend.model.HistorialCocina;
import cl.quehaypahacer.backend.model.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialCocinaRepository extends JpaRepository<HistorialCocina, Long> {

    List<HistorialCocina> findByUsuarioOrderByCocinadoAtDesc(Usuario usuario);

    @Query("""
        SELECT new cl.quehaypahacer.backend.dto.analitica.RecetaRankingDTO(
            h.receta.id,
            h.receta.titulo,
            COUNT(h),
            0.0
        )
        FROM HistorialCocina h
        WHERE h.cocinadoAt >= :desde
        GROUP BY h.receta.id, h.receta.titulo
        ORDER BY COUNT(h) DESC
    """)
    List<RecetaRankingDTO> findTopRecetasCocinadasDesde(@Param("desde") LocalDateTime desde, Pageable pageable);

    @Query("SELECT COUNT(h) FROM HistorialCocina h WHERE h.cocinadoAt >= :desde")
    long countCocinadasDesde(@Param("desde") LocalDateTime desde);

    @Query("""
        SELECT ir.ingrediente.nombre, COUNT(h)
        FROM HistorialCocina h
        JOIN h.receta r
        JOIN r.ingredientes ir
        WHERE ir.ingrediente.nombre IS NOT NULL
        GROUP BY ir.ingrediente.nombre
    """)
    List<Object[]> findConteoIngredientesCocinados();
}

