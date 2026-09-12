package cl.quehaypahacer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaComparadaDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String instrucciones;
    private Integer tiempoMinutos;
    private Integer porciones;
    private String dificultad;
    private String imagenUrl;
    
    private EstadoReceta estadoGeneral; // VERDE, AMARILLO, ROJO
    private Double porcentajeCoincidencia; // Ej: 100.0, 75.0, 0.0
    private Integer ingredientesVerdes;
    private Integer ingredientesAmarillos;
    private Integer ingredientesRojos;
    private Integer totalIngredientes;
    
    private List<ComparacionIngredienteDTO> ingredientes;
}
