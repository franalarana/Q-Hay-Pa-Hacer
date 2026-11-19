package cl.quehaypahacer.backend.admin.kafka.service;

import cl.quehaypahacer.backend.admin.kafka.dto.KafkaConsumerGroupDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicInfoDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicRequestDTO;

import java.util.List;

/**
 * Servicio de administración en caliente de componentes de Apache Kafka (Indicadores 3 y 8).
 */
public interface KafkaAdminService {

    List<KafkaTopicInfoDTO> listarTopicos();

    KafkaTopicInfoDTO obtenerTopico(String name);

    void crearTopico(KafkaTopicRequestDTO dto);

    void eliminarTopico(String name);

    List<KafkaConsumerGroupDTO> listarConsumerGroups();
}
