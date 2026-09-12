package cl.quehaypahacer.backend.repository;

import cl.quehaypahacer.backend.model.Receta;
import cl.quehaypahacer.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByCreadorIsNullOrderByTituloAsc();
    List<Receta> findByCreadorOrderByTituloAsc(Usuario creador);

    @Query("SELECT r FROM Receta r LEFT JOIN FETCH r.ingredientes ir LEFT JOIN FETCH ir.ingrediente")
    List<Receta> findAllWithIngredientes();
}
