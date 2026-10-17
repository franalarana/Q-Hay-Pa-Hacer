package cl.quehaypahacer.backend.admin.rabbitmq.dto;

public record QueueStatusDTO(String name, long messageCount, int consumerCount) {
}
