package cl.quehaypahacer.backend.dto;

import cl.quehaypahacer.backend.model.HistorialCocina;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialCocinaResponse {

    private Long id;
    private Long recetaId;
    private String tituloReceta;
    private String imagenUrl;
    private LocalDateTime cocinadoAt;
    private String notas;

    public static HistorialCocinaResponse fromEntity(HistorialCocina entity) {
        return HistorialCocinaResponse.builder()
                .id(entity.getId())
                .recetaId(entity.getReceta().getId())
                .tituloReceta(entity.getReceta().getTitulo())
                .imagenUrl(entity.getReceta().getImagenUrl())
                .cocinadoAt(entity.getCocinadoAt())
                .notas(entity.getNotas())
                .build();
    }
}
