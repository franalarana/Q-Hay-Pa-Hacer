package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.HistorialCocinaResponse;
import cl.quehaypahacer.backend.messaging.event.TicketCocinaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteCocinaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.publisher.KafkaAnaliticaEventPublisher;
import cl.quehaypahacer.backend.messaging.publisher.TicketEventPublisher;
import cl.quehaypahacer.backend.model.HistorialCocina;
import cl.quehaypahacer.backend.model.IngredienteReceta;
import cl.quehaypahacer.backend.model.Receta;
import cl.quehaypahacer.backend.model.Usuario;
import cl.quehaypahacer.backend.repository.HistorialCocinaRepository;
import cl.quehaypahacer.backend.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import cl.quehaypahacer.backend.dto.analitica.RecetaRankingDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistorialCocinaService {

    private final HistorialCocinaRepository historialCocinaRepository;
    private final RecetaRepository recetaRepository;
    private final UsuarioService usuarioService;
    private final TicketEventPublisher ticketEventPublisher;
    private final KafkaAnaliticaEventPublisher kafkaAnaliticaPublisher;

    @Transactional(readOnly = true)
    public List<RecetaRankingDTO> obtenerTopRecetasSemanal(int limite) {
        LocalDateTime desde = LocalDateTime.now().minusDays(7);
        long total = historialCocinaRepository.countCocinadasDesde(desde);
        List<RecetaRankingDTO> ranking = historialCocinaRepository.findTopRecetasCocinadasDesde(
                desde,
                PageRequest.of(0, Math.max(1, limite))
        );

        if (total == 0 || ranking.isEmpty()) {
            return List.of();
        }

        return ranking.stream()
                .map(r -> new RecetaRankingDTO(
                        r.recetaId(),
                        r.titulo(),
                        r.totalCocinadas(),
                        Math.round(((double) r.totalCocinadas() / total * 100.0) * 10.0) / 10.0
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HistorialCocinaResponse> obtenerHistorial(Jwt jwt) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        return historialCocinaRepository.findByUsuarioOrderByCocinadoAtDesc(usuario)
                .stream()
                .map(HistorialCocinaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public HistorialCocinaResponse registrarCocinado(Jwt jwt, Long recetaId, String notas) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada con id: " + recetaId));

        HistorialCocina nuevoRegistro = HistorialCocina.builder()
                .usuario(usuario)
                .receta(receta)
                .notas(notas)
                .build();

        HistorialCocina guardado = historialCocinaRepository.save(nuevoRegistro);
        ticketEventPublisher.publicarTicketCocina(new TicketCocinaEvent(
                guardado.getId(),
                receta.getId(),
                receta.getTitulo(),
                usuario.getCorreo(),
                guardado.getNotas(),
                guardado.getCocinadoAt()));

        if (receta.getIngredientes() != null) {
            for (IngredienteReceta ir : receta.getIngredientes()) {
                if (ir.getIngrediente() != null) {
                    kafkaAnaliticaPublisher.publicarEventoCocina(new IngredienteCocinaAnaliticaEvent(
                            receta.getId(),
                            receta.getTitulo(),
                            ir.getIngrediente().getId(),
                            ir.getIngrediente().getNombre(),
                            ir.getCantidadRequerida(),
                            ir.getUnidad() != null ? ir.getUnidad().name() : null,
                            usuario.getCorreo(),
                            LocalDateTime.now()
                    ));
                }
            }
        }

        return HistorialCocinaResponse.fromEntity(guardado);
    }
}
