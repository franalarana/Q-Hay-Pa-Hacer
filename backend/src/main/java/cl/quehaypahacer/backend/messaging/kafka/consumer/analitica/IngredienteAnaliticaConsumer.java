package cl.quehaypahacer.backend.messaging.kafka.consumer.analitica;

import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteCocinaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteDespensaAnaliticaEvent;
import cl.quehaypahacer.backend.service.IngredienteAnaliticaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka principal para agregación analítica (Indicadores 2, 5 y 7).
 * Pertenece al Consumer Group 'quehaypahacer-analitica-group'.
 * Consume desde los dos tópicos requeridos y delega la agregación a la capa de servicio.
 */
@Component
@RequiredArgsConstructor
public class IngredienteAnaliticaConsumer {

    private static final Logger log = LoggerFactory.getLogger(IngredienteAnaliticaConsumer.class);

    private final IngredienteAnaliticaService analiticaService;

    @KafkaListener(
            topics = "${app.kafka.topics.ingredientes-despensa}",
            groupId = "${app.kafka.consumer-groups.analitica}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirEventoDespensa(
            @Payload IngredienteDespensaAnaliticaEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA ANALITICA CONSUMER] Despensa -> Ingrediente: '{}', Acción: '{}', Partición: {}, Offset: {}",
                event.nombreIngrediente(), event.accion(), partition, offset);

        if (event.nombreIngrediente() == null || event.nombreIngrediente().isBlank()) {
            log.warn("[KAFKA ANALITICA WARN] Payload descartado por nombre vacío");
            return;
        }

        // Si es agregado o actualizado, suma a la presencia en despensa
        if (!IngredienteDespensaAnaliticaEvent.Accion.ELIMINADO.equalsIgnoreCase(event.accion())) {
            analiticaService.registrarIngredienteDespensa(event.nombreIngrediente());
        }
    }

    @KafkaListener(
            topics = "${app.kafka.topics.ingredientes-cocina}",
            groupId = "${app.kafka.consumer-groups.analitica}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirEventoCocina(
            @Payload IngredienteCocinaAnaliticaEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("[KAFKA ANALITICA CONSUMER] Cocina -> Ingrediente: '{}' de Receta: '{}', Partición: {}, Offset: {}",
                event.nombreIngrediente(), event.recetaTitulo(), partition, offset);

        if (event.nombreIngrediente() == null || event.nombreIngrediente().isBlank()) {
            log.warn("[KAFKA ANALITICA WARN] Payload de cocina descartado por nombre vacío");
            return;
        }

        analiticaService.registrarIngredienteCocina(event.nombreIngrediente());
    }
}
