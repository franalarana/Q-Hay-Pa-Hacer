package cl.quehaypahacer.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearRecetaRequest {

    @NotBlank(message = "El título de la receta es obligatorio")
    private String titulo;

    private String descripcion;

    @NotBlank(message = "Las instrucciones de preparación son obligatorias")
    private String instrucciones;

    @Positive(message = "El tiempo de preparación debe ser mayor a 0")
    private Integer tiempoMinutos;

    @Positive(message = "Las porciones deben ser mayor a 0")
    private Integer porciones;

    private String dificultad;

    private String imagenUrl;

    @NotEmpty(message = "La receta debe tener al menos un ingrediente")
    @Valid
    private List<IngredienteRecetaRequest> ingredientes;
}
