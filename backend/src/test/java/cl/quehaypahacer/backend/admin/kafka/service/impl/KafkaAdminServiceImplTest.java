package cl.quehaypahacer.backend.admin.kafka.service.impl;

import cl.quehaypahacer.backend.admin.kafka.dto.KafkaConsumerGroupDTO;
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
