package cl.quehaypahacer.backend.admin.kafka.controller;

import cl.quehaypahacer.backend.admin.kafka.dto.KafkaConsumerGroupDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaConsumerGroupDetailDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaPartitionOffsetDTO;
import cl.quehaypahacer.backend.admin.kafka.dto.KafkaTopicInfoDTO;
import cl.quehaypahacer.backend.admin.kafka.exception.KafkaAdminExceptionHandler;
import cl.quehaypahacer.backend.admin.kafka.exception.KafkaConsumerGroupNotFoundException;
import cl.quehaypahacer.backend.admin.kafka.exception.KafkaTopicNotFoundException;
import cl.quehaypahacer.backend.admin.kafka.service.KafkaAdminService;
import cl.quehaypahacer.backend.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KafkaAdminController.class)
@Import({SecurityConfig.class, KafkaAdminExceptionHandler.class})
class KafkaAdminControllerTest {

    private static final String BASE = "/api/admin/kafka";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaAdminService kafkaAdminService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static RequestPostProcessor admin() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_Admin"));
    }

    @Test
    void sinTokenDevuelve401() throws Exception {
        mockMvc.perform(get(BASE + "/topics")).andExpect(status().isUnauthorized());
    }

    @Test
    void sinRolAdminDevuelve403() throws Exception {
        mockMvc.perform(get(BASE + "/topics")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Usuario"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarTopicosDevuelveParticionesReplicasYConfig() throws Exception {
        when(kafkaAdminService.listarTopicos()).thenReturn(List.of(
                new KafkaTopicInfoDTO("analitica.despensa.ingredientes", 2, 1,
                        Map.of("retention.ms", "604800000", "cleanup.policy", "delete"))));

        mockMvc.perform(get(BASE + "/topics").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("analitica.despensa.ingredientes"))
                .andExpect(jsonPath("$[0].partitions").value(2))
                .andExpect(jsonPath("$[0].replicationFactor").value(1))
                .andExpect(jsonPath("$[0].configs['cleanup.policy']").value("delete"));
    }

    @Test
    void obtenerTopicoExistenteDevuelve200() throws Exception {
        when(kafkaAdminService.obtenerTopico("analitica.cocina.ingredientes"))
                .thenReturn(new KafkaTopicInfoDTO("analitica.cocina.ingredientes", 2, 1, Map.of()));

        mockMvc.perform(get(BASE + "/topics/analitica.cocina.ingredientes").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partitions").value(2));
    }

    @Test
    void topicoInexistenteDevuelve404() throws Exception {
        when(kafkaAdminService.obtenerTopico("no.existe"))
                .thenThrow(new KafkaTopicNotFoundException("Tópico 'no.existe' no encontrado"));

        mockMvc.perform(get(BASE + "/topics/no.existe").with(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Tópico 'no.existe' no encontrado"));
    }

    @Test
    void crearTopicoValidoDevuelve201() throws Exception {
        mockMvc.perform(post(BASE + "/topics").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"mi.topico\",\"partitions\":3,\"replicationFactor\":1,"
                                + "\"retentionMs\":86400000,\"cleanupPolicy\":\"delete\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("mi.topico"))
                .andExpect(jsonPath("$.particiones").value("3"));

        verify(kafkaAdminService).crearTopico(any());
    }

    @Test
    void crearTopicoSinParticionesDevuelve400() throws Exception {
        mockMvc.perform(post(BASE + "/topics").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"mi.topico\",\"partitions\":0,\"replicationFactor\":1}"))
                .andExpect(status().isBadRequest());

        verify(kafkaAdminService, never()).crearTopico(any());
    }

    @Test
    void crearTopicoConNombreInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post(BASE + "/topics").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"mi topico!\",\"partitions\":1,\"replicationFactor\":1}"))
                .andExpect(status().isBadRequest());

        verify(kafkaAdminService, never()).crearTopico(any());
    }

    @Test
    void jsonMalFormadoDevuelve400() throws Exception {
        mockMvc.perform(post(BASE + "/topics").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{no es json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void pathVariableInvalidaDevuelve400() throws Exception {
        mockMvc.perform(get(BASE + "/topics/mi$topico").with(admin()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void brokerRechazaCreacionDevuelve400() throws Exception {
        doThrow(new IllegalArgumentException("No se pudo crear el tópico en Kafka: TopicExistsException"))
                .when(kafkaAdminService).crearTopico(any());

        mockMvc.perform(post(BASE + "/topics").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"mi.topico\",\"partitions\":1,\"replicationFactor\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void eliminarTopicoDevuelve204() throws Exception {
        mockMvc.perform(delete(BASE + "/topics/mi.topico").with(admin()))
                .andExpect(status().isNoContent());

        verify(kafkaAdminService).eliminarTopico("mi.topico");
    }

    @Test
    void eliminarTopicoInexistenteDevuelve404() throws Exception {
        doThrow(new KafkaTopicNotFoundException("El tópico 'x' no existe"))
                .when(kafkaAdminService).eliminarTopico("x");

        mockMvc.perform(delete(BASE + "/topics/x").with(admin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarConsumerGroupsDevuelveEstado() throws Exception {
        when(kafkaAdminService.listarConsumerGroups()).thenReturn(List.of(
                new KafkaConsumerGroupDTO("quehaypahacer-analitica-group", "STABLE", false),
                new KafkaConsumerGroupDTO("quehaypahacer-auditoria-group", "STABLE", false)));

        mockMvc.perform(get(BASE + "/consumer-groups").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].groupId").value("quehaypahacer-analitica-group"))
                .andExpect(jsonPath("$[1].state").value("STABLE"));
    }

    @Test
    void actualizarConfiguracionDevuelveTopicoActualizado() throws Exception {
        when(kafkaAdminService.actualizarConfiguracion(eq("mi.topico"), any()))
                .thenReturn(new KafkaTopicInfoDTO("mi.topico", 2, 1, Map.of("retention.ms", "86400000")));

        mockMvc.perform(put(BASE + "/topics/mi.topico/config").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"retentionMs\":86400000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configs['retention.ms']").value("86400000"));
    }

    @Test
    void actualizarConfiguracionConCleanupInvalidoDevuelve400() throws Exception {
        mockMvc.perform(put(BASE + "/topics/mi.topico/config").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cleanupPolicy\":\"borrar\"}"))
                .andExpect(status().isBadRequest());

        verify(kafkaAdminService, never()).actualizarConfiguracion(any(), any());
    }

    @Test
    void actualizarConfiguracionConRetencionNegativaDevuelve400() throws Exception {
        mockMvc.perform(put(BASE + "/topics/mi.topico/config").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"retentionMs\":-5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aumentarParticionesDevuelveNuevoTotal() throws Exception {
        when(kafkaAdminService.aumentarParticiones("mi.topico", 4))
                .thenReturn(new KafkaTopicInfoDTO("mi.topico", 4, 1, Map.of()));

        mockMvc.perform(post(BASE + "/topics/mi.topico/partitions").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"totalPartitions\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partitions").value(4));
    }

    @Test
    void disminuirParticionesDevuelve400() throws Exception {
        when(kafkaAdminService.aumentarParticiones("mi.topico", 1))
                .thenThrow(new IllegalArgumentException("Kafka solo permite aumentar particiones"));

        mockMvc.perform(post(BASE + "/topics/mi.topico/partitions").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"totalPartitions\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void describirConsumerGroupDevuelveOffsetsYLag() throws Exception {
        when(kafkaAdminService.describirConsumerGroup("quehaypahacer-analitica-group"))
                .thenReturn(new KafkaConsumerGroupDetailDTO("quehaypahacer-analitica-group", "STABLE", 2, 5,
                        List.of(new KafkaPartitionOffsetDTO("analitica.cocina.ingredientes", 0, 10, 15, 5, "consumer-1"))));

        mockMvc.perform(get(BASE + "/consumer-groups/quehaypahacer-analitica-group").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members").value(2))
                .andExpect(jsonPath("$.totalLag").value(5))
                .andExpect(jsonPath("$.partitions[0].committedOffset").value(10))
                .andExpect(jsonPath("$.partitions[0].endOffset").value(15));
    }

    @Test
    void consumerGroupInexistenteDevuelve404() throws Exception {
        when(kafkaAdminService.describirConsumerGroup("fantasma"))
                .thenThrow(new KafkaConsumerGroupNotFoundException("El consumer group 'fantasma' no existe"));

        mockMvc.perform(get(BASE + "/consumer-groups/fantasma").with(admin()))
                .andExpect(status().isNotFound());
    }

    @Test
    void brokerCaidoDevuelve503() throws Exception {
        when(kafkaAdminService.listarTopicos())
                .thenThrow(new RuntimeException("Error al comunicarse con Kafka Broker: timeout"));

        mockMvc.perform(get(BASE + "/topics").with(admin()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());
    }
}
