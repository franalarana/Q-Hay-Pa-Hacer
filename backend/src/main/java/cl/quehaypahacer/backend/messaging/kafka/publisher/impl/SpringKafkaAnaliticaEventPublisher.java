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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Implementación de productor Kafka con KafkaTemplate tipado y logging contextual (Indicadores 1 y 4).
 * Utiliza el nombre del ingrediente como clave de particionado para garantizar orden relativo.
 * <p>
 * Convive con RabbitMQ sin afectarlo:
 * - Publica solo después del commit de la transacción (mismo patrón que los publishers RabbitMQ),
 *   por lo que una falla de Kafka nunca provoca rollback ni impide el envío de tickets/notificaciones.
 * - Cualquier excepción del envío se captura y se registra en log: el flujo del usuario continúa.
 * - El tiempo máximo de bloqueo del productor está acotado (max.block.ms en KafkaConfig).
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
        ejecutarDespuesDelCommit(() -> enviar(topic, event.nombreIngrediente(), event, "despensa"));
    }

    @Override
    public void publicarEventoCocina(IngredienteCocinaAnaliticaEvent event) {
        String topic = kafkaProperties.topics().ingredientesCocina();
        ejecutarDespuesDelCommit(() -> enviar(topic, event.nombreIngrediente(), event,
                "cocina (receta '" + event.recetaTitulo() + "')"));
    }

    private void enviar(String topic, String key, Object event, String tipo) {
        try {
            log.info("[KAFKA PUBLISHER] Publicando evento {} -> Tópico: '{}', Clave: '{}'", tipo, topic, key);
            kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("[KAFKA PUBLISHER WARN] No se pudo enviar evento {} con clave '{}': {}",
                            tipo, key, ex.getMessage());
                } else {
                    log.debug("[KAFKA PUBLISHER OK] Evento {} entregado en partición {}, offset {}",
                            tipo,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            // Kafka es analítica complementaria: su falla no debe interrumpir el flujo de negocio ni RabbitMQ
            log.error("[KAFKA PUBLISHER ERROR] Falla al publicar evento {} en '{}' (clave '{}'): {}",
                    tipo, topic, key, e.getMessage());
        }
    }

    private void ejecutarDespuesDelCommit(Runnable envio) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    envio.run();
                }
            });
        } else {
            envio.run();
        }
    }
}
