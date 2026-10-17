package cl.quehaypahacer.backend.admin.rabbitmq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ExchangeRequestDTO(
        @NotBlank(message = "El nombre del exchange es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Solo letras, números, punto, guion y guion bajo")
        String name,
        @NotBlank(message = "El tipo es obligatorio")
        @Pattern(regexp = "direct|topic|fanout|headers", message = "Tipo debe ser direct, topic, fanout o headers")
        String type,
        Boolean durable
) {
}
