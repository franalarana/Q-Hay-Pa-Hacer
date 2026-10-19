package cl.quehaypahacer.backend.admin.rabbitmq.service.impl;

import cl.quehaypahacer.backend.admin.rabbitmq.dto.BindingRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.ExchangeRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.QueueRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.QueueStatusDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.exception.RabbitResourceNotFoundException;
import cl.quehaypahacer.backend.admin.rabbitmq.exception.RabbitResourceProtectedException;
import cl.quehaypahacer.backend.admin.rabbitmq.service.RabbitAdminService;
import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import java.util.Set;

/** Encapsula RabbitAdmin: único punto del módulo admin que habla con el broker. */
@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitAdminServiceImpl implements RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;
    private final RabbitMQProperties props;

    @Override
    public void crearCola(QueueRequestDTO dto) {
        QueueBuilder builder = Boolean.TRUE.equals(dto.durable()) ? QueueBuilder.durable(dto.name()) : QueueBuilder.nonDurable(dto.name());
        if (Boolean.TRUE.equals(dto.autoDelete())) {
            builder.autoDelete();
        }
        if (notBlank(dto.deadLetterExchange())) {
            builder.deadLetterExchange(dto.deadLetterExchange());
        }
        if (notBlank(dto.deadLetterRoutingKey())) {
            builder.deadLetterRoutingKey(dto.deadLetterRoutingKey());
        }
        rabbitAdmin.declareQueue(builder.build());
        log.info("[ADMIN] Cola creada: {}", dto.name());
    }

    @Override
    public void eliminarCola(String nombre) {
        if (colasDelSistema().contains(nombre)) {
            throw new RabbitResourceProtectedException("La cola '" + nombre + "' es del sistema y no se puede eliminar");
        }
        // El borrado AMQP es idempotente: se verifica la existencia antes para poder responder 404.
        exigirColaExistente(nombre);
        rabbitAdmin.deleteQueue(nombre);
        log.info("[ADMIN] Cola eliminada: {}", nombre);
    }

    @Override
    public QueueStatusDTO obtenerEstadoCola(String nombre) {
        QueueInformation info = exigirColaExistente(nombre);
        return new QueueStatusDTO(info.getName(), info.getMessageCount(), info.getConsumerCount());
    }

    @Override
    public void crearExchange(ExchangeRequestDTO dto) {
        ExchangeBuilder builder = switch (dto.type()) {
            case "direct" -> ExchangeBuilder.directExchange(dto.name());
            case "topic" -> ExchangeBuilder.topicExchange(dto.name());
            case "fanout" -> ExchangeBuilder.fanoutExchange(dto.name());
            default -> ExchangeBuilder.headersExchange(dto.name());
        };
        rabbitAdmin.declareExchange(builder.durable(Boolean.TRUE.equals(dto.durable())).build());
        log.info("[ADMIN] Exchange creado: {} ({})", dto.name(), dto.type());
    }

    @Override
    public void eliminarExchange(String nombre) {
        if (exchangesDelSistema().contains(nombre)) {
            throw new RabbitResourceProtectedException(
                    "El exchange '" + nombre + "' es del sistema y no se puede eliminar");
        }
        // El borrado AMQP de exchange es idempotente: no distingue si existía.
        rabbitAdmin.deleteExchange(nombre);
        log.info("[ADMIN] Exchange eliminado: {}", nombre);
    }

    @Override
    public void crearBinding(BindingRequestDTO dto) {
        exigirColaExistente(dto.queue());
        rabbitAdmin.declareBinding(toBinding(dto));
        log.info("[ADMIN] Binding creado: {} -> {} [{}]", dto.exchange(), dto.queue(), dto.routingKey());
    }

    @Override
    public void eliminarBinding(BindingRequestDTO dto) {
        rabbitAdmin.removeBinding(toBinding(dto));
        log.info("[ADMIN] Binding eliminado: {} -> {} [{}]", dto.exchange(), dto.queue(), dto.routingKey());
    }

    private QueueInformation exigirColaExistente(String nombre) {
        QueueInformation info = rabbitAdmin.getQueueInfo(nombre);
        if (info == null) {
            throw new RabbitResourceNotFoundException("Cola '" + nombre + "' no existe");
        }
        return info;
    }

    private Binding toBinding(BindingRequestDTO dto) {
        return new Binding(dto.queue(), Binding.DestinationType.QUEUE, dto.exchange(), dto.routingKey(), null);
    }

    private Set<String> colasDelSistema() {
        RabbitMQProperties.Queues q = props.queues();
        return Set.of(
                q.tickets(), q.notificaciones(), q.favoritos(),
                q.ticketsDlq(), q.notificacionesDlq(), q.favoritosDlq()
        );
    }

    private Set<String> exchangesDelSistema() {
        return Set.of(props.exchanges().principal(), props.exchanges().deadLetter());
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}
