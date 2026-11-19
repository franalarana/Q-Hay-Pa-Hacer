package cl.quehaypahacer.backend.admin.kafka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manejador global de excepciones para las APIs administrativas de Kafka.
 */
@RestControllerAdvice(basePackages = "cl.quehaypahacer.backend.admin.kafka")
public class KafkaAdminExceptionHandler {

    @ExceptionHandler(KafkaTopicNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(KafkaTopicNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleServerError(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Error en servicio de Kafka: " + ex.getMessage()));
    }
}
