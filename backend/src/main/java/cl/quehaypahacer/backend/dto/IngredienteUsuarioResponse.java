package cl.quehaypahacer.backend.dto;

import cl.quehaypahacer.backend.model.IngredienteUsuario;
import cl.quehaypahacer.backend.model.UnidadMedida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredienteUsuarioResponse {

    private Long id;
    private Long ingredienteId;
    private String nombreIngrediente;
    private String categoria;
    private Double cantidad;
    private UnidadMedida unidad;
    private LocalDateTime updatedAt;

    public static IngredienteUsuarioResponse fromEntity(IngredienteUsuario entity) {
        return IngredienteUsuarioResponse.builder()
                .id(entity.getId())
                .ingredienteId(entity.getIngrediente().getId())
                .nombreIngrediente(entity.getIngrediente().getNombre())
                .categoria(entity.getIngrediente().getCategoria())
                .cantidad(entity.getCantidad())
                .unidad(entity.getUnidad())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
