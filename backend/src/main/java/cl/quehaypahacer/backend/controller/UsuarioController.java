package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.model.Usuario;
import cl.quehaypahacer.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Endpoint de verificación: si el JWT de Azure AD es válido, devuelve
     * el usuario local (creándolo si es la primera vez que inicia sesión).
     */
    @GetMapping("/api/me")
    public Usuario me(JwtAuthenticationToken auth) {
        Jwt jwt = auth.getToken();
        return usuarioService.obtenerOCrear(jwt);
    }
}
