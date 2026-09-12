package cl.quehaypahacer.backend.dto;

import cl.quehaypahacer.backend.model.UnidadMedida;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredienteRecetaRequest {

    @NotNull(message = "El id del ingrediente es obligatorio")
    private Long ingredienteId;

    @NotNull(message = "La cantidad requerida es obligatoria")
    @Positive(message = "La cantidad requerida debe ser mayor a 0")
    private Double cantidadRequerida;

    @NotNull(message = "La unidad de medida es obligatoria")
    private UnidadMedida unidad;

    @Builder.Default
    private Boolean opcional = false;
}
