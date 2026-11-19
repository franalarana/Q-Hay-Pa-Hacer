package cl.quehaypahacer.backend.messaging.kafka.consumer.auditoria;

import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteCocinaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteDespensaAnaliticaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Segundo grupo de consumidores ('quehaypahacer-auditoria-group') para telemetría y auditoría (Indicador 7).
 * Demuestra que múltiples consumer groups procesan los mismos flujos de datos de forma independiente
 * sin interferir en los offsets ni en el ritmo de consumo del grupo analítico.
 */
@Component
public class KafkaAuditoriaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaAuditoriaConsumer.class);

    @KafkaListener(
            topics = "${app.kafka.topics.ingredientes-despensa}",
            groupId = "${app.kafka.consumer-groups.auditoria}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void auditarEventoDespensa(
            @Payload IngredienteDespensaAnaliticaEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA AUDITORIA] Grupo '{}' registró evento despensa -> Tópico: {}, Partición: {}, Offset: {}, Usuario: {}",
                "quehaypahacer-auditoria-group", topic, partition, offset, event.usuarioCorreo());
    }

    @KafkaListener(
            topics = "${app.kafka.topics.ingredientes-cocina}",
            groupId = "${app.kafka.consumer-groups.auditoria}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void auditarEventoCocina(
            @Payload IngredienteCocinaAnaliticaEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA AUDITORIA] Grupo '{}' registró evento cocina -> Tópico: {}, Partición: {}, Offset: {}, Receta: '{}'",
                "quehaypahacer-auditoria-group", topic, partition, offset, event.recetaTitulo());
    }
}
