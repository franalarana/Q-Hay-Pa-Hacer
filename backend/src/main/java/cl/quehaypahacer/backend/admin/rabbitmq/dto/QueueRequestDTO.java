package cl.quehaypahacer.backend.admin.rabbitmq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record QueueRequestDTO(
        @NotBlank(message = "El nombre de la cola es obligatorio")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Solo letras, números, punto, guion y guion bajo")
        String name,
        Boolean durable,
        Boolean autoDelete,
        @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Exchange DLX con caracteres inválidos")
        String deadLetterExchange,
        @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Routing key DLX con caracteres inválidos")
        String deadLetterRoutingKey
) {
}
