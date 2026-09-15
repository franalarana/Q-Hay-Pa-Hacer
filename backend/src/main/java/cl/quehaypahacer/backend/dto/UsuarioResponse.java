package cl.quehaypahacer.backend.dto;

import cl.quehaypahacer.backend.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private LocalDateTime createdAt;
    private boolean esAdmin;

    public static UsuarioResponse fromEntity(Usuario usuario, boolean esAdmin) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .createdAt(usuario.getCreatedAt())
                .esAdmin(esAdmin)
                .build();
    }
}
