package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.IngredienteUsuarioRequest;
import cl.quehaypahacer.backend.dto.IngredienteUsuarioResponse;
import cl.quehaypahacer.backend.messaging.event.NotificacionDespensaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteDespensaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.publisher.KafkaAnaliticaEventPublisher;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespensaService {

    private final IngredienteUsuarioRepository ingredienteUsuarioRepository;
    private final IngredienteRepository ingredienteRepository;
    private final UsuarioService usuarioService;
    private final NotificacionEventPublisher notificacionEventPublisher;
    private final KafkaAnaliticaEventPublisher kafkaAnaliticaPublisher;
    private final IngredienteAnaliticaService analiticaService;

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
                    acumularCantidad(existente, request.getCantidad(), request.getUnidad());
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

        kafkaAnaliticaPublisher.publicarEventoDespensa(new IngredienteDespensaAnaliticaEvent(
                guardado.getIngrediente().getId(),
                guardado.getIngrediente().getNombre(),
                guardado.getCantidad(),
                guardado.getUnidad() != null ? guardado.getUnidad().name() : null,
                esNuevo ? IngredienteDespensaAnaliticaEvent.Accion.AGREGADO : IngredienteDespensaAnaliticaEvent.Accion.ACTUALIZADO,
                usuario.getCorreo(),
                LocalDateTime.now()
        ));

        analiticaService.registrarIngredienteDespensa(guardado.getIngrediente().getNombre());

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

        kafkaAnaliticaPublisher.publicarEventoDespensa(new IngredienteDespensaAnaliticaEvent(
                guardado.getIngrediente().getId(),
                guardado.getIngrediente().getNombre(),
                guardado.getCantidad(),
                guardado.getUnidad() != null ? guardado.getUnidad().name() : null,
                IngredienteDespensaAnaliticaEvent.Accion.ACTUALIZADO,
                usuario.getCorreo(),
                LocalDateTime.now()
        ));

        analiticaService.registrarIngredienteDespensa(guardado.getIngrediente().getNombre());

        return IngredienteUsuarioResponse.fromEntity(guardado);
    }

    @Transactional
    public void eliminarDeDespensa(Jwt jwt, Long id) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        IngredienteUsuario item = ingredienteUsuarioRepository.findByUsuarioAndId(usuario, id)
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente de despensa no encontrado con id: " + id));

        Long itemId = item.getId();
        Long ingredienteId = item.getIngrediente().getId();
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

        kafkaAnaliticaPublisher.publicarEventoDespensa(new IngredienteDespensaAnaliticaEvent(
                ingredienteId,
                ingredienteNombre,
                0.0,
                unidadNombre,
                IngredienteDespensaAnaliticaEvent.Accion.ELIMINADO,
                usuarioCorreo,
                LocalDateTime.now()
        ));
    }

    private void acumularCantidad(IngredienteUsuario existente, Double cantidadAgregada, UnidadMedida unidadAgregada) {
        double cantidadActual = existente.getCantidad() != null ? existente.getCantidad() : 0.0;
        UnidadMedida unidadActual = existente.getUnidad();

        if (unidadActual == null) {
            existente.setCantidad(redondearCantidad(cantidadAgregada));
            existente.setUnidad(unidadAgregada);
            return;
        }

        Optional<Double> agregadaEnUnidadActual = convertirSiCompatible(cantidadAgregada, unidadAgregada, unidadActual);
        if (agregadaEnUnidadActual.isPresent()) {
            existente.setCantidad(redondearCantidad(cantidadActual + agregadaEnUnidadActual.get()));
        } else {
            existente.setCantidad(redondearCantidad(cantidadAgregada));
            existente.setUnidad(unidadAgregada);
        }
    }

    private Optional<Double> convertirSiCompatible(Double cantidad, UnidadMedida origen, UnidadMedida destino) {
        if (origen == destino) {
            return Optional.of(cantidad);
        }

        Double factorMasaOrigen = factorMasaEnGramos(origen);
        Double factorMasaDestino = factorMasaEnGramos(destino);
        if (factorMasaOrigen != null && factorMasaDestino != null) {
            return Optional.of(cantidad * factorMasaOrigen / factorMasaDestino);
        }

        Double factorVolOrigen = factorVolumenEnMililitros(origen);
        Double factorVolDestino = factorVolumenEnMililitros(destino);
        if (factorVolOrigen != null && factorVolDestino != null) {
            return Optional.of(cantidad * factorVolOrigen / factorVolDestino);
        }

        return Optional.empty();
    }

    private Double factorMasaEnGramos(UnidadMedida unidad) {
        return switch (unidad) {
            case GRAMOS -> 1.0;
            case KILOGRAMOS -> 1000.0;
            default -> null;
        };
    }

    private Double factorVolumenEnMililitros(UnidadMedida unidad) {
        return switch (unidad) {
            case MILILITROS -> 1.0;
            case LITROS -> 1000.0;
            case TAZA -> 250.0;
            case CUCHARADA -> 15.0;
            case CUCHARADITA -> 5.0;
            default -> null;
        };
    }

    private double redondearCantidad(double cantidad) {
        return BigDecimal.valueOf(cantidad)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
