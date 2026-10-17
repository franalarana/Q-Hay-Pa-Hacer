package cl.quehaypahacer.backend.admin.rabbitmq.exception;

/** Se intenta borrar un recurso del sistema (definido en RabbitMQProperties). */
public class RabbitResourceProtectedException extends RuntimeException {

    public RabbitResourceProtectedException(String message) {
        super(message);
    }
}
