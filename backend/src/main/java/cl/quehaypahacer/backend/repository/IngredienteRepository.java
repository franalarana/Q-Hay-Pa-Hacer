package cl.quehaypahacer.backend.repository;

import cl.quehaypahacer.backend.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    List<Ingrediente> findAllByOrderByNombreAsc();
    Optional<Ingrediente> findByNombreIgnoreCase(String nombre);
    List<Ingrediente> findByCategoriaIgnoreCase(String categoria);
}
