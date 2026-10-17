package cl.quehaypahacer.backend.admin.rabbitmq.controller;

import cl.quehaypahacer.backend.admin.rabbitmq.dto.BindingRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.ExchangeRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.QueueRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.QueueStatusDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Administración en caliente de colas, exchanges y bindings. Solo delega en el servicio. */
@Validated
@RestController
@RequestMapping("/api/admin/rabbitmq")
@PreAuthorize("hasRole('Admin')")
@RequiredArgsConstructor
public class RabbitAdminController {

    private static final String NAME_REGEX = "^[a-zA-Z0-9._-]+$";
    private static final String NAME_MSG = "Solo letras, números, punto, guion y guion bajo";

    private final RabbitAdminService rabbitAdminService;

    @PostMapping("/queues")
    public ResponseEntity<Map<String, String>> crearCola(@Valid @RequestBody QueueRequestDTO dto) {
        rabbitAdminService.crearCola(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Cola creada", "nombre", dto.name()));
    }

    @GetMapping("/queues/{name}")
    public ResponseEntity<QueueStatusDTO> estadoCola(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String name) {
        return ResponseEntity.ok(rabbitAdminService.obtenerEstadoCola(name));
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Void> eliminarCola(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String name) {
        rabbitAdminService.eliminarCola(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, String>> crearExchange(@Valid @RequestBody ExchangeRequestDTO dto) {
        rabbitAdminService.crearExchange(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Exchange creado", "nombre", dto.name()));
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Void> eliminarExchange(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String name) {
        rabbitAdminService.eliminarExchange(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bindings")
    public ResponseEntity<Map<String, String>> crearBinding(@Valid @RequestBody BindingRequestDTO dto) {
        rabbitAdminService.crearBinding(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Binding creado"));
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Void> eliminarBinding(@Valid @RequestBody BindingRequestDTO dto) {
        rabbitAdminService.eliminarBinding(dto);
        return ResponseEntity.noContent().build();
    }
}
