package cl.quehaypahacer.backend.admin.kafka.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Map;

/**
 * Manejador global de excepciones para las APIs administrativas de Kafka.
 */
@RestControllerAdvice(basePackages = "cl.quehaypahacer.backend.admin.kafka")
public class KafkaAdminExceptionHandler {

    /** @PreAuthorize lanza AccessDeniedException (RuntimeException): sin esto caería en el 503 genérico. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Acceso denegado: requiere rol Admin"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleBadJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "JSON inválido"));
    }

    /** @Validated a nivel de clase valida los @PathVariable vía AOP y lanza esta excepción. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraint(ConstraintViolationException ex) {
        String detalle = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("Parámetro inválido");
        return ResponseEntity.badRequest().body(Map.of("error", "Validación fallida", "detalle", detalle));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handlePathValidation(HandlerMethodValidationException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Validación fallida"));
    }

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
