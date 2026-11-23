package cl.quehaypahacer.backend.admin.kafka.service.impl;

import cl.quehaypahacer.backend.admin.kafka.dto.KafkaConsumerGroupDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicConfigUpdateDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicInfoDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicRequestDTO;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaAdminServiceImplTest {

    @Mock
    private AdminClient adminClient;

    @InjectMocks
    private KafkaAdminServiceImpl adminService;

    @Test
    void crearTopico_llamaACreateTopicsEnAdminClient() {
        KafkaTopicRequestDTO dto = new KafkaTopicRequestDTO(
                "analitica.nuevo.topico", 3, (short) 1, 3600000L, "delete"
        );

        CreateTopicsResult createTopicsResult = mock(CreateTopicsResult.class);
        when(createTopicsResult.all()).thenReturn(KafkaFuture.completedFuture(null));
        when(adminClient.createTopics(any())).thenReturn(createTopicsResult);

        assertDoesNotThrow(() -> adminService.crearTopico(dto));
        verify(adminClient).createTopics(any());
    }

    @Test
    void eliminarTopico_llamaADeleteTopicsEnAdminClient() {
        DeleteTopicsResult deleteTopicsResult = mock(DeleteTopicsResult.class);
        when(deleteTopicsResult.all()).thenReturn(KafkaFuture.completedFuture(null));
        when(adminClient.deleteTopics(anyCollection())).thenReturn(deleteTopicsResult);

        assertDoesNotThrow(() -> adminService.eliminarTopico("topico.a.eliminar"));
        verify(adminClient).deleteTopics(anyCollection());
    }

    /** Simula que el tópico existe con N particiones (lo usa obtenerTopicInfo). */
    private void topicoExiste(String name, int particiones) {
        List<TopicPartitionInfo> parts = new ArrayList<>();
        Node nodo = new Node(1, "localhost", 9092);
        for (int i = 0; i < particiones; i++) {
            parts.add(new TopicPartitionInfo(i, nodo, List.of(nodo), List.of(nodo)));
        }
        DescribeTopicsResult describe = mock(DescribeTopicsResult.class);
        when(describe.allTopicNames()).thenReturn(
                KafkaFuture.completedFuture(Map.of(name, new TopicDescription(name, false, parts))));
        when(adminClient.describeTopics(anyCollection())).thenReturn(describe);

        DescribeConfigsResult configs = mock(DescribeConfigsResult.class);
        ConfigResource recurso = new ConfigResource(ConfigResource.Type.TOPIC, name);
        when(configs.all()).thenReturn(KafkaFuture.completedFuture(Map.of(recurso, new Config(List.of()))));
        when(adminClient.describeConfigs(anyCollection())).thenReturn(configs);
    }

    @Test
    void actualizarConfiguracion_llamaAIncrementalAlterConfigs() {
        topicoExiste("mi.topico", 2);
        AlterConfigsResult alter = mock(AlterConfigsResult.class);
        when(alter.all()).thenReturn(KafkaFuture.completedFuture(null));
        when(adminClient.incrementalAlterConfigs(anyMap())).thenReturn(alter);

        assertDoesNotThrow(() -> adminService.actualizarConfiguracion("mi.topico",
                new KafkaTopicConfigUpdateDTO(86400000L, "delete")));
        verify(adminClient).incrementalAlterConfigs(anyMap());
    }

    @Test
    void actualizarConfiguracion_sinCambiosLanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> adminService.actualizarConfiguracion("mi.topico", new KafkaTopicConfigUpdateDTO(null, null)));
        verify(adminClient, never()).incrementalAlterConfigs(anyMap());
    }

    @Test
    void aumentarParticiones_llamaACreatePartitions() {
        topicoExiste("mi.topico", 2);
        CreatePartitionsResult result = mock(CreatePartitionsResult.class);
        when(result.all()).thenReturn(KafkaFuture.completedFuture(null));
        when(adminClient.createPartitions(anyMap())).thenReturn(result);

        assertDoesNotThrow(() -> adminService.aumentarParticiones("mi.topico", 4));
        verify(adminClient).createPartitions(anyMap());
    }

    @Test
    void aumentarParticiones_rechazaDisminuir() {
        topicoExiste("mi.topico", 3);

        assertThrows(IllegalArgumentException.class, () -> adminService.aumentarParticiones("mi.topico", 2));
        verify(adminClient, never()).createPartitions(anyMap());
    }

    @Test
    void listarConsumerGroups_retornaListaDeGrupos() {
        ListConsumerGroupsResult listResult = mock(ListConsumerGroupsResult.class);
        ConsumerGroupListing groupListing = new ConsumerGroupListing("test-group", false);
        when(listResult.all()).thenReturn(KafkaFuture.completedFuture(Collections.singletonList(groupListing)));
        when(adminClient.listConsumerGroups()).thenReturn(listResult);

        List<KafkaConsumerGroupDTO> grupos = adminService.listarConsumerGroups();

        assertNotNull(grupos);
        assertEquals(1, grupos.size());
        assertEquals("test-group", grupos.get(0).groupId());
    }
}
