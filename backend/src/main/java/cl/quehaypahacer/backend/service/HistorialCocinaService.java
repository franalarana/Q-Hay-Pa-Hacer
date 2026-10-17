package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.HistorialCocinaResponse;
import cl.quehaypahacer.backend.messaging.event.TicketCocinaEvent;
import cl.quehaypahacer.backend.messaging.publisher.TicketEventPublisher;
import cl.quehaypahacer.backend.model.HistorialCocina;
import cl.quehaypahacer.backend.model.Receta;
import cl.quehaypahacer.backend.model.Usuario;
import cl.quehaypahacer.backend.repository.HistorialCocinaRepository;
import cl.quehaypahacer.backend.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistorialCocinaService {

    private final HistorialCocinaRepository historialCocinaRepository;
    private final RecetaRepository recetaRepository;
    private final UsuarioService usuarioService;
    private final TicketEventPublisher ticketEventPublisher;

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

        return HistorialCocinaResponse.fromEntity(guardado);
    }
}
