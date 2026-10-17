package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.IngredienteUsuarioRequest;
import cl.quehaypahacer.backend.dto.IngredienteUsuarioResponse;
import cl.quehaypahacer.backend.messaging.event.NotificacionDespensaEvent;
import cl.quehaypahacer.backend.messaging.publisher.NotificacionEventPublisher;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespensaService {

    private final IngredienteUsuarioRepository ingredienteUsuarioRepository;
    private final IngredienteRepository ingredienteRepository;
    private final UsuarioService usuarioService;
    private final NotificacionEventPublisher notificacionEventPublisher;

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

        boolean esNuevo = (item.getId() == null);
        IngredienteUsuario guardado = ingredienteUsuarioRepository.save(item);

        notificacionEventPublisher.publicarNotificacionDespensa(new NotificacionDespensaEvent(
                guardado.getId(),
                guardado.getIngrediente().getNombre(),
                guardado.getCantidad(),
                guardado.getUnidad() != null ? guardado.getUnidad().name() : null,
                esNuevo ? NotificacionDespensaEvent.Accion.AGREGADO : NotificacionDespensaEvent.Accion.ACTUALIZADO,
                usuario.getCorreo(),
                guardado.getUpdatedAt() != null ? guardado.getUpdatedAt() : LocalDateTime.now()
        ));

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
        IngredienteUsuario guardado = ingredienteUsuarioRepository.save(item);

        notificacionEventPublisher.publicarNotificacionDespensa(new NotificacionDespensaEvent(
                guardado.getId(),
                guardado.getIngrediente().getNombre(),
                guardado.getCantidad(),
                guardado.getUnidad() != null ? guardado.getUnidad().name() : null,
                NotificacionDespensaEvent.Accion.ACTUALIZADO,
                usuario.getCorreo(),
                guardado.getUpdatedAt() != null ? guardado.getUpdatedAt() : LocalDateTime.now()
        ));

        return IngredienteUsuarioResponse.fromEntity(guardado);
    }

    @Transactional
    public void eliminarDeDespensa(Jwt jwt, Long id) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        IngredienteUsuario item = ingredienteUsuarioRepository.findByUsuarioAndId(usuario, id)
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente de despensa no encontrado con id: " + id));

        Long itemId = item.getId();
        String ingredienteNombre = item.getIngrediente().getNombre();
        String unidadNombre = item.getUnidad() != null ? item.getUnidad().name() : null;
        String usuarioCorreo = usuario.getCorreo();

        ingredienteUsuarioRepository.delete(item);

        notificacionEventPublisher.publicarNotificacionDespensa(new NotificacionDespensaEvent(
                itemId,
                ingredienteNombre,
                null,
                unidadNombre,
                NotificacionDespensaEvent.Accion.ELIMINADO,
                usuarioCorreo,
                LocalDateTime.now()
        ));
    }
}
