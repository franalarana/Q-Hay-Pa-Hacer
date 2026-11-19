package cl.quehaypahacer.backend.admin.kafka.service.impl;

import cl.quehaypahacer.backend.admin.kafka.dto.KafkaConsumerGroupDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicInfoDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicRequestDTO;
import cl.quehaypahacer.backend.admin.kafka.exception.KafkaTopicNotFoundException;
import cl.quehaypahacer.backend.admin.kafka.service.KafkaAdminService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Implementación de administración en caliente de Kafka mediante AdminClient nativo.
 */
@Service
@RequiredArgsConstructor
public class KafkaAdminServiceImpl implements KafkaAdminService {

    private static final Logger log = LoggerFactory.getLogger(KafkaAdminServiceImpl.class);
    private static final long TIMEOUT_SECONDS = 5;

    private final AdminClient adminClient;

    @Override
    public List<KafkaTopicInfoDTO> listarTopicos() {
        try {
            Set<String> topicNames = adminClient.listTopics().names().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            List<KafkaTopicInfoDTO> resultado = new ArrayList<>();
            for (String name : topicNames) {
                // Omitir tópicos internos si los hubiera
                if (!name.startsWith("_")) {
                    resultado.add(obtenerTopicInfo(name));
                }
            }
            return resultado;
        } catch (Exception e) {
            log.error("[KAFKA ADMIN] Error al listar tópicos: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con Kafka Broker: " + e.getMessage(), e);
        }
    }

    @Override
    public KafkaTopicInfoDTO obtenerTopico(String name) {
        return obtenerTopicInfo(name);
    }

    @Override
    public void crearTopico(KafkaTopicRequestDTO dto) {
        try {
            Map<String, String> configs = new HashMap<>();
            if (dto.retentionMs() != null) {
                configs.put(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(dto.retentionMs()));
            }
            if (dto.cleanupPolicy() != null && !dto.cleanupPolicy().isBlank()) {
                configs.put(TopicConfig.CLEANUP_POLICY_CONFIG, dto.cleanupPolicy());
            }

            NewTopic newTopic = new NewTopic(dto.name(), dto.partitions(), dto.replicationFactor())
                    .configs(configs);

            adminClient.createTopics(Collections.singletonList(newTopic)).all().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.info("[KAFKA ADMIN] Tópico '{}' creado exitosamente con {} particiones y réplica {}",
                    dto.name(), dto.partitions(), dto.replicationFactor());
        } catch (Exception e) {
            log.error("[KAFKA ADMIN] Error al crear tópico '{}': {}", dto.name(), e.getMessage());
            throw new IllegalArgumentException("No se pudo crear el tópico en Kafka: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarTopico(String name) {
        try {
            adminClient.deleteTopics(Collections.singletonList(name)).all().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.info("[KAFKA ADMIN] Tópico '{}' eliminado exitosamente", name);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                throw new KafkaTopicNotFoundException("El tópico '" + name + "' no existe");
            }
            throw new RuntimeException("Error al eliminar tópico: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("[KAFKA ADMIN] Error al eliminar tópico '{}': {}", name, e.getMessage());
            throw new RuntimeException("Error al eliminar tópico: " + e.getMessage(), e);
        }
    }

    @Override
    public List<KafkaConsumerGroupDTO> listarConsumerGroups() {
        try {
            var listings = adminClient.listConsumerGroups().all().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            List<KafkaConsumerGroupDTO> grupos = new ArrayList<>();
            for (ConsumerGroupListing listing : listings) {
                grupos.add(new KafkaConsumerGroupDTO(
                        listing.groupId(),
                        listing.state().map(Enum::name).orElse("UNKNOWN"),
                        listing.isSimpleConsumerGroup()
                ));
            }
            return grupos;
        } catch (Exception e) {
            log.error("[KAFKA ADMIN] Error al listar grupos de consumidores: {}", e.getMessage());
            throw new RuntimeException("Error al listar consumer groups: " + e.getMessage(), e);
        }
    }

    private KafkaTopicInfoDTO obtenerTopicInfo(String name) {
        try {
            Map<String, TopicDescription> descriptions = adminClient.describeTopics(Collections.singletonList(name))
                    .allTopicNames().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            TopicDescription desc = descriptions.get(name);
            if (desc == null) {
                throw new KafkaTopicNotFoundException("Tópico '" + name + "' no encontrado");
            }

            int partitions = desc.partitions().size();
            int replicas = desc.partitions().isEmpty() ? 1 : desc.partitions().get(0).replicas().size();

            // Configuraciones de retención / cleanup
            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, name);
            Map<ConfigResource, Config> configsMap = adminClient.describeConfigs(Collections.singletonList(resource))
                    .all().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            Map<String, String> configMap = new HashMap<>();
            Config config = configsMap.get(resource);
            if (config != null) {
                for (ConfigEntry entry : config.entries()) {
                    if (TopicConfig.RETENTION_MS_CONFIG.equals(entry.name()) ||
                        TopicConfig.CLEANUP_POLICY_CONFIG.equals(entry.name()) ||
                        TopicConfig.SEGMENT_BYTES_CONFIG.equals(entry.name())) {
                        configMap.put(entry.name(), entry.value());
                    }
                }
            }

            return new KafkaTopicInfoDTO(name, partitions, replicas, configMap);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                throw new KafkaTopicNotFoundException("Tópico '" + name + "' no encontrado");
            }
            throw new RuntimeException("Error al describir tópico '" + name + "': " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar tópico '" + name + "': " + e.getMessage(), e);
        }
    }
}
