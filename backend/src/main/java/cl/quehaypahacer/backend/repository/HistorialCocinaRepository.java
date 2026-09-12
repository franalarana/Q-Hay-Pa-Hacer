package cl.quehaypahacer.backend.repository;

import cl.quehaypahacer.backend.model.HistorialCocina;
import cl.quehaypahacer.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialCocinaRepository extends JpaRepository<HistorialCocina, Long> {
    List<HistorialCocina> findByUsuarioOrderByCocinadoAtDesc(Usuario usuario);
}
