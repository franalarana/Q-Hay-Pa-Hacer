package cl.quehaypahacer.backend.dto;

import cl.quehaypahacer.backend.model.UnidadMedida;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComparacionIngredienteDTO {

    private Long ingredienteId;
    private String nombreIngrediente;
    private String categoria;
    private Double cantidadRequerida;
    private UnidadMedida unidadRequerida;
    private Double cantidadDisponible;
    private UnidadMedida unidadDisponible;
    private EstadoIngrediente estado; // VERDE, AMARILLO, ROJO
    private Boolean opcional;
    private String mensajeEstado; // Ej: "Suficiente", "Tienes 1 ud (falta 1 ud)", "No lo tienes"
}
