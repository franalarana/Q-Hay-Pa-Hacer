package cl.quehaypahacer.backend.admin.kafka.exception;

public class KafkaConsumerGroupNotFoundException extends RuntimeException {
    public KafkaConsumerGroupNotFoundException(String message) {
        super(message);
    }
}
