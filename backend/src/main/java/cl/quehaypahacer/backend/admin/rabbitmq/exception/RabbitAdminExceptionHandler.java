package cl.quehaypahacer.backend.admin.rabbitmq.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpIOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Limitado al módulo admin para no alterar las respuestas de los demás endpoints. */
@Slf4j
@RestControllerAdvice(basePackages = "cl.quehaypahacer.backend.admin.rabbitmq")
public class RabbitAdminExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> campos.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("error", "Validación fallida", "campos", campos));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePathValidation(HandlerMethodValidationException ex) {
        String detalle = ex.getParameterValidationResults().stream()
                .flatMap(r -> r.getResolvableErrors().stream())
                .map(e -> e.getDefaultMessage())
                .findFirst()
                .orElse("Parámetro inválido");
        return ResponseEntity.badRequest().body(Map.of("error", "Validación fallida", "detalle", detalle));
    }

    /** @Validated a nivel de clase valida los @PathVariable vía AOP y lanza esta excepción. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex) {
        String detalle = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("Parámetro inválido");
        return ResponseEntity.badRequest().body(Map.of("error", "Validación fallida", "detalle", detalle));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "JSON inválido"));
    }

    @ExceptionHandler(RabbitResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RabbitResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RabbitResourceProtectedException.class)
    public ResponseEntity<Map<String, Object>> handleProtected(RabbitResourceProtectedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    /** El broker respondió pero rechazó la operación (p. ej. cola ya existe con otros argumentos). */
    @ExceptionHandler(AmqpIOException.class)
    public ResponseEntity<Map<String, Object>> handleRejected(AmqpIOException ex) {
        log.warn("[ADMIN] Operación rechazada por el broker: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "El broker rechazó la operación (recurso inexistente o con configuración distinta)"));
    }

    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<Map<String, Object>> handleAmqp(AmqpException ex) {
        log.error("[ADMIN] Error AMQP: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Broker RabbitMQ no disponible"));
    }
}
