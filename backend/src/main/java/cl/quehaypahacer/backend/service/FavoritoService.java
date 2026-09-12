package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.dto.RecetaComparadaDTO;
import cl.quehaypahacer.backend.model.Favorito;
import cl.quehaypahacer.backend.model.Receta;
import cl.quehaypahacer.backend.model.Usuario;
import cl.quehaypahacer.backend.repository.FavoritoRepository;
import cl.quehaypahacer.backend.repository.RecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final RecetaRepository recetaRepository;
    private final UsuarioService usuarioService;
    private final ComparadorRecetasService comparadorRecetasService;

    @Transactional(readOnly = true)
    public List<Long> obtenerIdsFavoritos(Jwt jwt) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        return favoritoRepository.findByUsuarioOrderByCreatedAtDesc(usuario)
                .stream()
                .map(f -> f.getReceta().getId())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecetaComparadaDTO> obtenerRecetasFavoritas(Jwt jwt) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        List<Favorito> favoritos = favoritoRepository.findByUsuarioOrderByCreatedAtDesc(usuario);
        
        return favoritos.stream()
                .map(f -> comparadorRecetasService.obtenerDetalleReceta(jwt, f.getReceta().getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void alternarFavorito(Jwt jwt, Long recetaId) {
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada con id: " + recetaId));

        favoritoRepository.findByUsuarioAndReceta(usuario, receta)
                .ifPresentOrElse(
                        favoritoRepository::delete,
                        () -> favoritoRepository.save(Favorito.builder()
                                .usuario(usuario)
                                .receta(receta)
                                .build())
                );
    }
}
