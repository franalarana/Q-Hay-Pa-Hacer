package cl.quehaypahacer.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_cocina")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialCocina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @Column(name = "cocinado_at", nullable = false)
    @Builder.Default
    private LocalDateTime cocinadoAt = LocalDateTime.now();

    @Column(length = 500)
    private String notas;
}
