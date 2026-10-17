package cl.quehaypahacer.backend.admin.rabbitmq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BindingRequestDTO(
        @NotBlank(message = "La cola es obligatoria")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Nombre de cola inválido")
        String queue,
        @NotBlank(message = "El exchange es obligatorio")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Nombre de exchange inválido")
        String exchange,
        @NotBlank(message = "La routing key es obligatoria")
        @Pattern(regexp = "^[a-zA-Z0-9._*#-]+$", message = "Routing key inválida")
        String routingKey
) {
}
