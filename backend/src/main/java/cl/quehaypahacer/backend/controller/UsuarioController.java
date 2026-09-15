package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.dto.UsuarioResponse;
import cl.quehaypahacer.backend.model.Usuario;
import cl.quehaypahacer.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Endpoint de verificación: si el JWT de Azure AD es válido, devuelve
     * el usuario local (creándolo si es la primera vez que inicia sesión)
     * junto con "esAdmin", para que el frontend sepa si debe habilitar
     * acciones de administrador (editar/eliminar cualquier receta).
     */
    @GetMapping("/api/me")
    public UsuarioResponse me(JwtAuthenticationToken auth) {
        Jwt jwt = auth.getToken();
        Usuario usuario = usuarioService.obtenerOCrear(jwt);
        boolean esAdmin = jwt.getClaimAsStringList("roles") != null
                && jwt.getClaimAsStringList("roles").contains("Admin");
        return UsuarioResponse.fromEntity(usuario, esAdmin);
    }

    /**
     * Endpoint exclusivo para el rol Admin (App Role de Entra ID): lista
     * todos los usuarios registrados. Un token válido con rol Usuario
     * recibe 403 acá, sin depender de ownership.
     */
    @GetMapping("/api/usuarios")
    @PreAuthorize("hasRole('Admin')")
    public List<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos().stream()
                .map(u -> UsuarioResponse.fromEntity(u, false))
                .toList();
    }
}
