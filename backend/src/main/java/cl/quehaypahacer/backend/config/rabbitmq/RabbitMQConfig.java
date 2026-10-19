package cl.quehaypahacer.backend.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Infraestructura AMQP declarada como beans de Spring (Indicador 2).
 * <p>
 * Topología:
 * <pre>
 *  recetas.exchange (topic)
 *     ├── recetas.ticket.generar     ──► recetas.tickets.queue
 *     └── recetas.despensa.notificar ──► recetas.notificaciones.queue
 *
 *  recetas.dlx.exchange (direct)  [recibe lo rechazado con basicNack]
 *     ├── recetas.ticket.dlq         ──► recetas.tickets.dlq
 *     └── recetas.despensa.dlq       ──► recetas.notificaciones.dlq
 * </pre>
 * Esta clase solo contiene configuración: no hay lógica de negocio aquí
 * (Indicador 3). Los nombres vienen de {@link RabbitMQProperties}.
 */
@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMQConfig {

    /** Paquete de los eventos que se permiten deserializar desde JSON. */
    private static final String EVENTS_PACKAGE = "cl.quehaypahacer.backend.messaging.event";

    private final RabbitMQProperties props;

    public RabbitMQConfig(RabbitMQProperties props) {
        this.props = props;
    }

    // ------------------------------------------------------------------
    // Exchanges
    // ------------------------------------------------------------------

    @Bean
    public TopicExchange recetasExchange() {
        return new TopicExchange(props.exchanges().principal(), true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(props.exchanges().deadLetter(), true, false);
    }

    // ------------------------------------------------------------------
    // Colas de trabajo (con argumentos x-dead-letter-*)
    // ------------------------------------------------------------------

    @Bean
    public Queue ticketsQueue() {
        return QueueBuilder.durable(props.queues().tickets())
                .deadLetterExchange(props.exchanges().deadLetter())
                .deadLetterRoutingKey(props.routingKeys().ticketsDlq())
                .build();
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(props.queues().notificaciones())
                .deadLetterExchange(props.exchanges().deadLetter())
                .deadLetterRoutingKey(props.routingKeys().notificacionesDlq())
                .build();
    }

    @Bean
    public Queue favoritosQueue() {
        return QueueBuilder.durable(props.queues().favoritos())
                .deadLetterExchange(props.exchanges().deadLetter())
                .deadLetterRoutingKey(props.routingKeys().favoritosDlq())
                .build();
    }

    // ------------------------------------------------------------------
    // Dead Letter Queues
    // ------------------------------------------------------------------

    @Bean
    public Queue ticketsDlq() {
        return QueueBuilder.durable(props.queues().ticketsDlq()).build();
    }

    @Bean
    public Queue notificacionesDlq() {
        return QueueBuilder.durable(props.queues().notificacionesDlq()).build();
    }

    @Bean
    public Queue favoritosDlq() {
        return QueueBuilder.durable(props.queues().favoritosDlq()).build();
    }

    // ------------------------------------------------------------------
    // Bindings
    // ------------------------------------------------------------------

    @Bean
    public Binding ticketsBinding() {
        return BindingBuilder.bind(ticketsQueue())
                .to(recetasExchange())
                .with(props.routingKeys().tickets());
    }

    @Bean
    public Binding notificacionesBinding() {
        return BindingBuilder.bind(notificacionesQueue())
                .to(recetasExchange())
                .with(props.routingKeys().notificaciones());
    }

    @Bean
    public Binding favoritosBinding() {
        return BindingBuilder.bind(favoritosQueue())
                .to(recetasExchange())
                .with(props.routingKeys().favoritos());
    }

    @Bean
    public Binding ticketsDlqBinding() {
        return BindingBuilder.bind(ticketsDlq())
                .to(deadLetterExchange())
                .with(props.routingKeys().ticketsDlq());
    }

    @Bean
    public Binding notificacionesDlqBinding() {
        return BindingBuilder.bind(notificacionesDlq())
                .to(deadLetterExchange())
                .with(props.routingKeys().notificacionesDlq());
    }

    @Bean
    public Binding favoritosDlqBinding() {
        return BindingBuilder.bind(favoritosDlq())
                .to(deadLetterExchange())
                .with(props.routingKeys().favoritosDlq());
    }

    // ------------------------------------------------------------------
    // Serialización, plantilla de envío y administrador
    // ------------------------------------------------------------------

    /**
     * Convierte los eventos a JSON (Jackson 3, Spring AMQP 4).
     * Solo se aceptan clases del paquete de eventos al deserializar.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter(EVENTS_PACKAGE);
    }

    /** RabbitTemplate que usarán los publicadores (Parte 2). */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    /**
     * RabbitAdmin (implementación de AmqpAdmin). Declara automáticamente en el
     * broker todas las colas/exchanges/bindings de arriba al conectarse, y será
     * usado por RabbitAdminService (Parte 4) para crear/eliminar en caliente.
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
