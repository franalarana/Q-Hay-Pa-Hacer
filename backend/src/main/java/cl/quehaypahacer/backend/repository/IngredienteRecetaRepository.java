package cl.quehaypahacer.backend.repository;

import cl.quehaypahacer.backend.model.IngredienteReceta;
import cl.quehaypahacer.backend.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredienteRecetaRepository extends JpaRepository<IngredienteReceta, Long> {
    List<IngredienteReceta> findByReceta(Receta receta);
}
