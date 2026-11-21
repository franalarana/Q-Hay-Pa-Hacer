package cl.quehaypahacer.backend.repository;

import cl.quehaypahacer.backend.model.Ingrediente;
import cl.quehaypahacer.backend.model.IngredienteUsuario;
import cl.quehaypahacer.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredienteUsuarioRepository extends JpaRepository<IngredienteUsuario, Long> {
    List<IngredienteUsuario> findByUsuarioOrderByIngredienteNombreAsc(Usuario usuario);
    Optional<IngredienteUsuario> findByUsuarioAndIngrediente(Usuario usuario, Ingrediente ingrediente);
    Optional<IngredienteUsuario> findByUsuarioAndId(Usuario usuario, Long id);
    void deleteByUsuarioAndId(Usuario usuario, Long id);

    @Query("""
        SELECT iu.ingrediente.nombre, COUNT(iu)
        FROM IngredienteUsuario iu
        WHERE iu.ingrediente.nombre IS NOT NULL
        GROUP BY iu.ingrediente.nombre
    """)
    List<Object[]> findConteoIngredientesDespensa();
}

