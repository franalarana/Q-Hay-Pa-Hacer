package cl.quehaypahacer.backend.messaging.kafka.publisher.impl;

import cl.quehaypahacer.backend.config.kafka.KafkaProperties;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteCocinaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteDespensaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.publisher.KafkaAnaliticaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Implementación de productor Kafka con KafkaTemplate tipado y logging contextual (Indicadores 1 y 4).
 * Utiliza el nombre del ingrediente como clave de particionado para garantizar orden relativo.
 */
@Component
@RequiredArgsConstructor
public class SpringKafkaAnaliticaEventPublisher implements KafkaAnaliticaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SpringKafkaAnaliticaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    @Override
    public void publicarEventoDespensa(IngredienteDespensaAnaliticaEvent event) {
        String topic = kafkaProperties.topics().ingredientesDespensa();
        String key = event.nombreIngrediente();

        log.info("[KAFKA PUBLISHER] Publicando evento despensa -> Tópico: '{}', Clave: '{}', Ingrediente: '{}'",
                topic, key, event.nombreIngrediente());

        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.warn("[KAFKA PUBLISHER WARN] No se pudo enviar evento despensa para '{}': {}",
                        event.nombreIngrediente(), ex.getMessage());
            } else {
                log.debug("[KAFKA PUBLISHER OK] Evento despensa entregado en partición {}, offset {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    @Override
    public void publicarEventoCocina(IngredienteCocinaAnaliticaEvent event) {
        String topic = kafkaProperties.topics().ingredientesCocina();
        String key = event.nombreIngrediente();

        log.info("[KAFKA PUBLISHER] Publicando evento cocina -> Tópico: '{}', Clave: '{}', Ingrediente: '{}' (Receta: '{}')",
                topic, key, event.nombreIngrediente(), event.recetaTitulo());

        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.warn("[KAFKA PUBLISHER WARN] No se pudo enviar evento cocina para '{}': {}",
                        event.nombreIngrediente(), ex.getMessage());
            } else {
                log.debug("[KAFKA PUBLISHER OK] Evento cocina entregado en partición {}, offset {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
