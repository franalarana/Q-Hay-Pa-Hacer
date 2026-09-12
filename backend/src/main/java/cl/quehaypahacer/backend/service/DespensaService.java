package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.IngredienteUsuarioRequest;
import cl.quehaypahacer.backend.dto.IngredienteUsuarioResponse;
import cl.quehaypahacer.backend.model.Ingrediente;
import cl.quehaypahacer.backend.model.IngredienteUsuario;
import cl.quehaypahacer.backend.model.UnidadMedida;
import cl.quehaypahacer.backend.model.Usuario;
import cl.quehaypahacer.backend.repository.IngredienteRepository;
import cl.quehaypahacer.backend.repository.IngredienteUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespensaService {

    private final IngredienteUsuarioRepository ingredienteUsuarioRepository;
    private final IngredienteRepository ingredienteRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<IngredienteUsuarioResponse> obtenerDespensa(Jwt jwt) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        return ingredienteUsuarioRepository.findByUsuarioOrderByIngredienteNombreAsc(usuario)
                .stream()
                .map(IngredienteUsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredienteUsuarioResponse agregarOActualizar(Jwt jwt, IngredienteUsuarioRequest request) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        Ingrediente ingrediente = ingredienteRepository.findById(request.getIngredienteId())
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado con id: " + request.getIngredienteId()));

        IngredienteUsuario item = ingredienteUsuarioRepository
                .findByUsuarioAndIngrediente(usuario, ingrediente)
                .map(existente -> {
                    existente.setCantidad(request.getCantidad());
                    existente.setUnidad(request.getUnidad());
                    return existente;
                })
                .orElseGet(() -> IngredienteUsuario.builder()
                        .usuario(usuario)
                        .ingrediente(ingrediente)
                        .cantidad(request.getCantidad())
                        .unidad(request.getUnidad())
                        .build());

        IngredienteUsuario guardado = ingredienteUsuarioRepository.save(item);
        return IngredienteUsuarioResponse.fromEntity(guardado);
    }

    @Transactional
    public IngredienteUsuarioResponse actualizarCantidad(Jwt jwt, Long id, Double cantidad, UnidadMedida unidad) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        IngredienteUsuario item = ingredienteUsuarioRepository.findByUsuarioAndId(usuario, id)
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente de despensa no encontrado con id: " + id));

        item.setCantidad(cantidad);
        if (unidad != null) {
            item.setUnidad(unidad);
        }
        return IngredienteUsuarioResponse.fromEntity(ingredienteUsuarioRepository.save(item));
    }

    @Transactional
    public void eliminarDeDespensa(Jwt jwt, Long id) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        IngredienteUsuario item = ingredienteUsuarioRepository.findByUsuarioAndId(usuario, id)
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente de despensa no encontrado con id: " + id));
        ingredienteUsuarioRepository.delete(item);
    }
}
