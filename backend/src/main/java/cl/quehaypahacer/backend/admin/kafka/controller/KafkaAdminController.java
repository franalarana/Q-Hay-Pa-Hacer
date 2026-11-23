package cl.quehaypahacer.backend.admin.kafka.controller;

import cl.quehaypahacer.backend.admin.kafka.dto.KafkaConsumerGroupDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaConsumerGroupDetailDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaPartitionsRequestDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicConfigUpdateDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicInfoDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicRequestDTO;
import cl.quehaypahacer.backend.admin.kafka.service.KafkaAdminService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Endpoints REST para administración en caliente de tópicos y grupos de Kafka (Indicadores 3 y 8).
 * Requiere rol Admin mediante token JWT de Azure AD.
 */
@Validated
@RestController
@RequestMapping("/api/admin/kafka")
@PreAuthorize("hasRole('Admin')")
@RequiredArgsConstructor
public class KafkaAdminController {

    private static final String NAME_REGEX = "^[a-zA-Z0-9._-]+$";
    private static final String NAME_MSG = "Solo letras, números, punto, guion y guion bajo";

    private final KafkaAdminService kafkaAdminService;

    @GetMapping("/topics")
    public ResponseEntity<List<KafkaTopicInfoDTO>> listarTopicos() {
        return ResponseEntity.ok(kafkaAdminService.listarTopicos());
    }

    @GetMapping("/topics/{name}")
    public ResponseEntity<KafkaTopicInfoDTO> obtenerTopico(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String name) {
        return ResponseEntity.ok(kafkaAdminService.obtenerTopico(name));
    }

    @PostMapping("/topics")
    public ResponseEntity<Map<String, String>> crearTopico(@Valid @RequestBody KafkaTopicRequestDTO dto) {
        kafkaAdminService.crearTopico(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Tópico creado exitosamente",
                "nombre", dto.name(),
                "particiones", String.valueOf(dto.partitions()),
                "replicas", String.valueOf(dto.replicationFactor())
        ));
    }

    @PutMapping("/topics/{name}/config")
    public ResponseEntity<KafkaTopicInfoDTO> actualizarConfiguracion(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String name,
            @Valid @RequestBody KafkaTopicConfigUpdateDTO dto) {
        return ResponseEntity.ok(kafkaAdminService.actualizarConfiguracion(name, dto));
    }

    @PostMapping("/topics/{name}/partitions")
    public ResponseEntity<KafkaTopicInfoDTO> aumentarParticiones(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String name,
            @Valid @RequestBody KafkaPartitionsRequestDTO dto) {
        return ResponseEntity.ok(kafkaAdminService.aumentarParticiones(name, dto.totalPartitions()));
    }

    @DeleteMapping("/topics/{name}")
    public ResponseEntity<Void> eliminarTopico(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String name) {
        kafkaAdminService.eliminarTopico(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/consumer-groups")
    public ResponseEntity<List<KafkaConsumerGroupDTO>> listarConsumerGroups() {
        return ResponseEntity.ok(kafkaAdminService.listarConsumerGroups());
    }

    @GetMapping("/consumer-groups/{groupId}")
    public ResponseEntity<KafkaConsumerGroupDetailDTO> describirConsumerGroup(
            @PathVariable @Pattern(regexp = NAME_REGEX, message = NAME_MSG) String groupId) {
        return ResponseEntity.ok(kafkaAdminService.describirConsumerGroup(groupId));
    }
}
