package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.dto.IngredienteUsuarioRequest;
import cl.quehaypahacer.backend.dto.IngredienteUsuarioResponse;
import cl.quehaypahacer.backend.model.UnidadMedida;
import cl.quehaypahacer.backend.service.DespensaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/despensa")
@RequiredArgsConstructor
public class DespensaController {

    private final DespensaService despensaService;

    @GetMapping
    public List<IngredienteUsuarioResponse> obtenerDespensa(JwtAuthenticationToken auth) {
        return despensaService.obtenerDespensa(auth.getToken());
    }

    @PostMapping
    public ResponseEntity<IngredienteUsuarioResponse> agregarOActualizar(
            JwtAuthenticationToken auth,
            @Valid @RequestBody IngredienteUsuarioRequest request) {
        IngredienteUsuarioResponse response = despensaService.agregarOActualizar(auth.getToken(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IngredienteUsuarioResponse> actualizarCantidad(
            JwtAuthenticationToken auth,
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        Double cantidad = Double.valueOf(payload.get("cantidad").toString());
        UnidadMedida unidad = payload.containsKey("unidad") && payload.get("unidad") != null
                ? UnidadMedida.valueOf(payload.get("unidad").toString())
                : null;

        IngredienteUsuarioResponse response = despensaService.actualizarCantidad(auth.getToken(), id, cantidad, unidad);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(JwtAuthenticationToken auth, @PathVariable Long id) {
        despensaService.eliminarDeDespensa(auth.getToken(), id);
        return ResponseEntity.noContent().build();
    }
}
