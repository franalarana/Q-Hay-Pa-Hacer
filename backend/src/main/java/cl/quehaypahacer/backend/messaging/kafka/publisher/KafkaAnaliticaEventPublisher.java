package cl.quehaypahacer.backend.messaging.kafka.publisher;

import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteCocinaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteDespensaAnaliticaEvent;

/**
 * Contrato para publicar eventos analíticos hacia Apache Kafka (Indicador 4).
 * Desacopla la capa de negocio de la infraestructura de streaming.
 */
public interface KafkaAnaliticaEventPublisher {

    /**
     * Publica un evento de actividad de despensa en el tópico correspondiente.
     */
    void publicarEventoDespensa(IngredienteDespensaAnaliticaEvent event);

    /**
     * Publica un evento de ingrediente consumido al cocinar en el tópico correspondiente.
     */
    void publicarEventoCocina(IngredienteCocinaAnaliticaEvent event);
}
