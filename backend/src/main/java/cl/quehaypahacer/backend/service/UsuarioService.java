package cl.quehaypahacer.backend.service;

import cl.quehaypahacer.backend.model.Usuario;
import cl.quehaypahacer.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Provisioning "just in time": la primera vez que un usuario autenticado
     * por Azure AD llega al backend, se crea su fila local (despensa,
     * favoritos e historial se asocian a este id, no a una password propia).
     */
    public Usuario obtenerOCrear(Jwt jwt) {
        String oid = jwt.getClaimAsString("oid");
        if (oid == null) {
            oid = jwt.getSubject();
        }
        String finalOid = oid;

        return usuarioRepository.findByAzureOid(finalOid)
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario();
                    nuevo.setAzureOid(finalOid);
                    nuevo.setNombre(primerNoNulo(jwt.getClaimAsString("name"), "Usuario"));
                    nuevo.setCorreo(primerNoNulo(
                            jwt.getClaimAsString("preferred_username"),
                            jwt.getClaimAsString("email"),
                            "sin-correo@quehaypahacer.cl"
                    ));
                    return usuarioRepository.save(nuevo);
                });
    }

    private String primerNoNulo(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }
}
