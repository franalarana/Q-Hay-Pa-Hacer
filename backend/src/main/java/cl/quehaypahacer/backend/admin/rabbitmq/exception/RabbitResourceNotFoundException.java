package cl.quehaypahacer.backend.admin.rabbitmq.exception;

public class RabbitResourceNotFoundException extends RuntimeException {

    public RabbitResourceNotFoundException(String message) {
        super(message);
    }
}
