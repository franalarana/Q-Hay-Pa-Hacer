package cl.quehaypahacer.backend.admin.rabbitmq.controller;

import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import cl.quehaypahacer.backend.messaging.event.NotificacionDespensaEvent;
import cl.quehaypahacer.backend.messaging.event.TicketCocinaEvent;
import cl.quehaypahacer.backend.messaging.publisher.NotificacionEventPublisher;
import cl.quehaypahacer.backend.messaging.publisher.TicketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador de prueba para verificar publicación de eventos y comportamiento
 * de Dead Letter Queue (DLQ) en RabbitMQ.
 * <p>
 * Requiere rol Admin (Azure AD). Solo delega en las interfaces publisher,
 * sin interactuar directamente con RabbitTemplate.
 */
@RestController
@RequestMapping("/api/admin/rabbitmq/test")
@PreAuthorize("hasRole('Admin')")
@RequiredArgsConstructor
public class RabbitTestController {

    private final TicketEventPublisher ticketEventPublisher;
    private final NotificacionEventPublisher notificacionEventPublisher;
    private final RabbitMQProperties props;

    /**
     * Publica un TicketCocinaEvent válido (recetaId = 1) para verificar
     * procesamiento exitoso y confirmación (ACK) por parte del consumidor.
     */
    @PostMapping("/ticket-valido")
    public ResponseEntity<Map<String, String>> publicarTicketValido() {
        TicketCocinaEvent event = new TicketCocinaEvent(
                999L,
                1L,
                "Receta de Prueba Válida",
                "admin@quehaypahacer.cl",
                "Prueba de publicación manual - flujo normal",
                LocalDateTime.now()
        );
        ticketEventPublisher.publicarTicketCocina(event);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "mensaje", "Evento válido publicado",
                "routingKey", props.routingKeys().tickets()
        ));
    }

    /**
     * Publica un TicketCocinaEvent inválido (recetaId = -1) para forzar
     * un rechazo (NACK) con requeue=false en el consumidor y enviarlo a la DLQ.
     */
    @PostMapping("/ticket-invalido")
    public ResponseEntity<Map<String, String>> publicarTicketInvalido() {
        TicketCocinaEvent event = new TicketCocinaEvent(
                999L,
                -1L,
                "Receta de Prueba Inválida",
                "admin@quehaypahacer.cl",
                "Prueba de envío forzado a Dead Letter Queue (DLQ)",
                LocalDateTime.now()
        );
        ticketEventPublisher.publicarTicketCocina(event);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "mensaje", "Evento inválido publicado",
                "routingKey", props.routingKeys().tickets()
        ));
    }

    /**
     * Publica un NotificacionDespensaEvent inválido (accion = null) para forzar
     * el rechazo y direccionamiento a recetas.notificaciones.dlq.
     */
    @PostMapping("/notificacion-invalida")
    public ResponseEntity<Map<String, String>> publicarNotificacionInvalida() {
        NotificacionDespensaEvent event = new NotificacionDespensaEvent(
                999L,
                "Ingrediente Desconocido",
                1.0,
                "UNIDAD",
                null,
                "admin@quehaypahacer.cl",
                LocalDateTime.now()
        );
        notificacionEventPublisher.publicarNotificacionDespensa(event);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "mensaje", "Notificación inválida publicada",
                "routingKey", props.routingKeys().notificaciones()
        ));
    }
}
