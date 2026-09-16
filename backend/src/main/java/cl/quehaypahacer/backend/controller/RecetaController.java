package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.dto.CrearRecetaRequest;
import cl.quehaypahacer.backend.dto.RecetaComparadaDTO;
import cl.quehaypahacer.backend.repository.IngredienteRepository;
import cl.quehaypahacer.backend.service.ComparadorRecetasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final ComparadorRecetasService comparadorRecetasService;
    private final IngredienteRepository ingredienteRepository;

    @GetMapping("/sugeridas")
    public List<RecetaComparadaDTO> obtenerRecetasSugeridas(JwtAuthenticationToken auth) {
        return comparadorRecetasService.obtenerRecetasSugeridas(auth.getToken());
    }

    @GetMapping("/mis-recetas")
    public List<RecetaComparadaDTO> obtenerMisRecetas(JwtAuthenticationToken auth) {
        return comparadorRecetasService.obtenerMisRecetas(auth.getToken());
    }

    @GetMapping("/{id}")
    public RecetaComparadaDTO obtenerDetalleReceta(JwtAuthenticationToken auth, @PathVariable Long id) {
        return comparadorRecetasService.obtenerDetalleReceta(auth.getToken(), id);
    }

    @PostMapping
    public ResponseEntity<RecetaComparadaDTO> crearRecetaPropia(
            JwtAuthenticationToken auth,
            @Valid @RequestBody CrearRecetaRequest request) {
        RecetaComparadaDTO creada = comparadorRecetasService.crearRecetaPropia(auth.getToken(), request, ingredienteRepository);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public RecetaComparadaDTO actualizarRecetaPropia(
            JwtAuthenticationToken auth,
            @PathVariable Long id,
            @Valid @RequestBody CrearRecetaRequest request) {
        return comparadorRecetasService.actualizarRecetaPropia(auth.getToken(), id, request, ingredienteRepository);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRecetaPropia(JwtAuthenticationToken auth, @PathVariable Long id) {
        comparadorRecetasService.eliminarRecetaPropia(auth.getToken(), id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint exclusivo para Administradores (evidencia de rúbrica 403)
    @GetMapping("/admin/reportes")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> obtenerReportesAdmin() {
        return ResponseEntity.ok("Datos clasificados de administrador");
    }
}
