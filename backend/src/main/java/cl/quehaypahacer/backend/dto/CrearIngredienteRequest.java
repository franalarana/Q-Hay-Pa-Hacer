package cl.quehaypahacer.backend.dto;

import cl.quehaypahacer.backend.model.UnidadMedida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearIngredienteRequest {

    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    private String nombre;

    @NotBlank(message = "La categoría del ingrediente es obligatoria")
    private String categoria;

    @NotNull(message = "La unidad base es obligatoria")
    private UnidadMedida unidadBase;
}
