package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.dto.HistorialCocinaResponse;
import cl.quehaypahacer.backend.service.HistorialCocinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialCocinaService historialCocinaService;

    @GetMapping
    public List<HistorialCocinaResponse> obtenerHistorial(JwtAuthenticationToken auth) {
        return historialCocinaService.obtenerHistorial(auth.getToken());
    }

    @PostMapping("/{recetaId}")
    public ResponseEntity<HistorialCocinaResponse> registrarCocinado(
            JwtAuthenticationToken auth,
            @PathVariable Long recetaId,
            @RequestBody(required = false) Map<String, String> payload) {
        String notas = (payload != null) ? payload.get("notas") : null;
        HistorialCocinaResponse response = historialCocinaService.registrarCocinado(auth.getToken(), recetaId, notas);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
