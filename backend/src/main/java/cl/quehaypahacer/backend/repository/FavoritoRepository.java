package cl.quehaypahacer.backend.repository;

import cl.quehaypahacer.backend.model.Favorito;
import cl.quehaypahacer.backend.model.Receta;
import cl.quehaypahacer.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuarioOrderByCreatedAtDesc(Usuario usuario);
    Optional<Favorito> findByUsuarioAndReceta(Usuario usuario, Receta receta);
    boolean existsByUsuarioAndReceta(Usuario usuario, Receta receta);
    void deleteByUsuarioAndReceta(Usuario usuario, Receta receta);
}
