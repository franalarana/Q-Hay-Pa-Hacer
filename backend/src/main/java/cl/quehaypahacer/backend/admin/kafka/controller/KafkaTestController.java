package cl.quehaypahacer.backend.admin.kafka.controller;

import cl.quehaypahacer.backend.config.kafka.KafkaProperties;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteCocinaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteDespensaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.publisher.KafkaAnaliticaEventPublisher;
import cl.quehaypahacer.backend.service.IngredienteAnaliticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador de pruebas para simular flujos de streaming en Kafka y verificar reintentos y error handling.
 * Requiere rol Admin (Azure AD).
 */
@RestController
@RequestMapping("/api/admin/kafka/test")
@PreAuthorize("hasRole('Admin')")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaAnaliticaEventPublisher publisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties props;
    private final IngredienteAnaliticaService analiticaService;

    @PostMapping("/evento-despensa")
    public ResponseEntity<Map<String, String>> testEventoDespensa(
            @RequestParam(defaultValue = "Tomate") String ingrediente,
            @RequestParam(defaultValue = "1.5") Double cantidad,
            @RequestParam(defaultValue = "KILOGRAMO") String unidad) {

        IngredienteDespensaAnaliticaEvent event = new IngredienteDespensaAnaliticaEvent(
                100L,
                ingrediente,
                cantidad,
                unidad,
                IngredienteDespensaAnaliticaEvent.Accion.AGREGADO,
                "admin@quehaypahacer.cl",
                LocalDateTime.now()
        );
        publisher.publicarEventoDespensa(event);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Evento de despensa emitido a Kafka",
                "ingrediente", ingrediente,
                "topico", props.topics().ingredientesDespensa()
        ));
    }

    @PostMapping("/evento-cocina")
    public ResponseEntity<Map<String, String>> testEventoCocina(
            @RequestParam(defaultValue = "Cazuela de Vacuno") String receta,
            @RequestParam(defaultValue = "Carne") String ingrediente) {

        IngredienteCocinaAnaliticaEvent event = new IngredienteCocinaAnaliticaEvent(
                1L,
                receta,
                200L,
                ingrediente,
                500.0,
                "GRAMO",
                "admin@quehaypahacer.cl",
                LocalDateTime.now()
        );
        publisher.publicarEventoCocina(event);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Evento de cocina emitido a Kafka",
                "receta", receta,
                "ingrediente", ingrediente,
                "topico", props.topics().ingredientesCocina()
        ));
    }

    @PostMapping("/evento-invalido")
    public ResponseEntity<Map<String, String>> testEventoInvalido() {
        // Enviar un mensaje con payload vacío o corrupto directamente al tópico para verificar DefaultErrorHandler
        kafkaTemplate.send(props.topics().ingredientesDespensa(), "test-error", "{\"ingredienteId\": null, \"nombreIngrediente\": \"\"}");
        return ResponseEntity.ok(Map.of(
                "mensaje", "Payload corrupto/inválido enviado para verificar DefaultErrorHandler y logging contextual",
                "topico", props.topics().ingredientesDespensa()
        ));
    }

    @PostMapping("/reset-estadisticas")
    public ResponseEntity<Map<String, String>> resetEstadisticas() {
        analiticaService.reiniciarEstadisticas();
        return ResponseEntity.ok(Map.of("mensaje", "Estadísticas analíticas reiniciadas"));
    }
}
