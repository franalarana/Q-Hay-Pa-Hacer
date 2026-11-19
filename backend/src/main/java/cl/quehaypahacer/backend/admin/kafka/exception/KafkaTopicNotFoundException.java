package cl.quehaypahacer.backend.admin.kafka.exception;

public class KafkaTopicNotFoundException extends RuntimeException {
    public KafkaTopicNotFoundException(String message) {
        super(message);
    }
}
