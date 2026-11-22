package cl.quehaypahacer.backend.admin.kafka.controller;

import cl.quehaypahacer.backend.config.kafka.KafkaProperties;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteCocinaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.event.IngredienteDespensaAnaliticaEvent;
import cl.quehaypahacer.backend.messaging.kafka.publisher.KafkaAnaliticaEventPublisher;
import cl.quehaypahacer.backend.security.SecurityConfig;
import cl.quehaypahacer.backend.service.IngredienteAnaliticaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KafkaTestController.class)
@Import(SecurityConfig.class)
class KafkaTestControllerSecurityTest {

    private static final String BASE = "/api/admin/kafka/test";
    private static final String TOPICO_DESPENSA = "analitica.despensa.ingredientes";
    private static final String TOPICO_COCINA = "analitica.cocina.ingredientes";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaAnaliticaEventPublisher publisher;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private KafkaProperties props;

    @MockitoBean
    private IngredienteAnaliticaService analiticaService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        when(props.topics()).thenReturn(new KafkaProperties.Topics(TOPICO_DESPENSA, TOPICO_COCINA));
    }

    private static RequestPostProcessor admin() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_Admin"));
    }

    @Test
    void sinTokenDevuelve401() throws Exception {
        mockMvc.perform(post(BASE + "/evento-despensa")).andExpect(status().isUnauthorized());
        verify(publisher, never()).publicarEventoDespensa(any());
    }

    @Test
    void sinRolAdminDevuelve403() throws Exception {
        mockMvc.perform(post(BASE + "/evento-despensa")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Usuario"))))
                .andExpect(status().isForbidden());
        verify(publisher, never()).publicarEventoDespensa(any());
    }

    @Test
    void eventoDespensaPublicaDtoTipadoEnTopicoDespensa() throws Exception {
        mockMvc.perform(post(BASE + "/evento-despensa").with(admin())
                        .param("ingrediente", "Cebolla")
                        .param("cantidad", "2.0")
                        .param("unidad", "UNIDAD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topico").value(TOPICO_DESPENSA))
                .andExpect(jsonPath("$.ingrediente").value("Cebolla"));

        ArgumentCaptor<IngredienteDespensaAnaliticaEvent> captor =
                ArgumentCaptor.forClass(IngredienteDespensaAnaliticaEvent.class);
        verify(publisher).publicarEventoDespensa(captor.capture());
        assertThat(captor.getValue().nombreIngrediente()).isEqualTo("Cebolla");
        assertThat(captor.getValue().cantidad()).isEqualTo(2.0);
        assertThat(captor.getValue().accion()).isEqualTo(IngredienteDespensaAnaliticaEvent.Accion.AGREGADO);
    }

    @Test
    void eventoCocinaPublicaDtoTipadoEnTopicoCocina() throws Exception {
        mockMvc.perform(post(BASE + "/evento-cocina").with(admin())
                        .param("receta", "Charquicán")
                        .param("ingrediente", "Zapallo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topico").value(TOPICO_COCINA));

        ArgumentCaptor<IngredienteCocinaAnaliticaEvent> captor =
                ArgumentCaptor.forClass(IngredienteCocinaAnaliticaEvent.class);
        verify(publisher).publicarEventoCocina(captor.capture());
        assertThat(captor.getValue().recetaTitulo()).isEqualTo("Charquicán");
        assertThat(captor.getValue().nombreIngrediente()).isEqualTo("Zapallo");
    }

    @Test
    void eventoInvalidoSeEnviaDirectoAlTopicoDespensa() throws Exception {
        mockMvc.perform(post(BASE + "/evento-invalido").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topico").value(TOPICO_DESPENSA));

        verify(kafkaTemplate).send(eq(TOPICO_DESPENSA), eq("test-error"), anyString());
    }

    @Test
    void resetEstadisticasLimpiaContadores() throws Exception {
        mockMvc.perform(post(BASE + "/reset-estadisticas").with(admin()))
                .andExpect(status().isOk());

        verify(analiticaService).reiniciarEstadisticas();
    }
}
