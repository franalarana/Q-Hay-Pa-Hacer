package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.dto.RecetaComparadaDTO;
import cl.quehaypahacer.backend.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    @GetMapping("/ids")
    public List<Long> obtenerIdsFavoritos(JwtAuthenticationToken auth) {
        return favoritoService.obtenerIdsFavoritos(auth.getToken());
    }

    @GetMapping
    public List<RecetaComparadaDTO> obtenerRecetasFavoritas(JwtAuthenticationToken auth) {
        return favoritoService.obtenerRecetasFavoritas(auth.getToken());
    }

    @PostMapping("/{recetaId}")
    public ResponseEntity<Void> alternarFavorito(JwtAuthenticationToken auth, @PathVariable Long recetaId) {
        favoritoService.alternarFavorito(auth.getToken(), recetaId);
        return ResponseEntity.ok().build();
    }
}
