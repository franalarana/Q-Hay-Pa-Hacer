package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.dto.RecetaComparadaDTO;
import cl.quehaypahacer.backend.service.ComparadorRecetasService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final ComparadorRecetasService comparadorRecetasService;

    @GetMapping("/sugeridas")
    public List<RecetaComparadaDTO> obtenerRecetasSugeridas(JwtAuthenticationToken auth) {
        return comparadorRecetasService.obtenerRecetasSugeridas(auth.getToken());
    }

    @GetMapping("/{id}")
    public RecetaComparadaDTO obtenerDetalleReceta(JwtAuthenticationToken auth, @PathVariable Long id) {
        return comparadorRecetasService.obtenerDetalleReceta(auth.getToken(), id);
    }
}
