package cl.quehaypahacer.backend.admin.rabbitmq.controller;

import cl.quehaypahacer.backend.admin.rabbitmq.dto.QueueStatusDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.exception.RabbitAdminExceptionHandler;
import cl.quehaypahacer.backend.admin.rabbitmq.exception.RabbitResourceNotFoundException;
import cl.quehaypahacer.backend.admin.rabbitmq.exception.RabbitResourceProtectedException;
import cl.quehaypahacer.backend.admin.rabbitmq.service.RabbitAdminService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RabbitAdminController.class)
@Import({SecurityConfig.class, RabbitAdminExceptionHandler.class})
class RabbitAdminControllerTest {

    private static final String BASE = "/api/admin/rabbitmq";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RabbitAdminService rabbitAdminService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static org.springframework.test.web.servlet.request.RequestPostProcessor admin() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_Admin"));
    }

    @Test
    void sinTokenDevuelve401() throws Exception {
        mockMvc.perform(get(BASE + "/queues/mi.cola")).andExpect(status().isUnauthorized());
    }

    @Test
    void sinRolAdminDevuelve403() throws Exception {
        mockMvc.perform(get(BASE + "/queues/mi.cola")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Usuario"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearColaValidaDevuelve201() throws Exception {
        mockMvc.perform(post(BASE + "/queues").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"mi.cola\",\"durable\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("mi.cola"));

        verify(rabbitAdminService).crearCola(any());
    }

    @Test
    void nombreVacioDevuelve400ConDetalleDelCampo() throws Exception {
        mockMvc.perform(post(BASE + "/queues").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.campos.name").exists());
    }

    @Test
    void nombreConCaracteresInvalidosDevuelve400() throws Exception {
        mockMvc.perform(post(BASE + "/queues").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"mi cola!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void jsonMalFormadoDevuelve400() throws Exception {
        mockMvc.perform(post(BASE + "/queues").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("JSON inválido"));
    }

    @Test
    void tipoDeExchangeInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post(BASE + "/exchanges").with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"ex\",\"type\":\"raro\",\"durable\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.type").exists());
    }

    @Test
    void pathVariableInvalidaDevuelve400() throws Exception {
        mockMvc.perform(get(BASE + "/queues/mi$cola").with(admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validación fallida"));
    }

    @Test
    void estadoDeColaDevuelveConteos() throws Exception {
        when(rabbitAdminService.obtenerEstadoCola("recetas.tickets.queue"))
                .thenReturn(new QueueStatusDTO("recetas.tickets.queue", 3, 1));

        mockMvc.perform(get(BASE + "/queues/recetas.tickets.queue").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageCount").value(3))
                .andExpect(jsonPath("$.consumerCount").value(1));
    }

    @Test
    void colaInexistenteDevuelve404() throws Exception {
        when(rabbitAdminService.obtenerEstadoCola("x"))
                .thenThrow(new RabbitResourceNotFoundException("Cola 'x' no existe"));

        mockMvc.perform(get(BASE + "/queues/x").with(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cola 'x' no existe"));
    }

    @Test
    void eliminarColaDevuelve204() throws Exception {
        mockMvc.perform(delete(BASE + "/queues/mi.cola").with(admin()))
                .andExpect(status().isNoContent());

        verify(rabbitAdminService).eliminarCola("mi.cola");
    }

    @Test
    void eliminarColaDelSistemaDevuelve409() throws Exception {
        doThrow(new RabbitResourceProtectedException("es del sistema"))
                .when(rabbitAdminService).eliminarCola("recetas.tickets.queue");

        mockMvc.perform(delete(BASE + "/queues/recetas.tickets.queue").with(admin()))
                .andExpect(status().isConflict());
    }

    @Test
    void crearYEliminarBinding() throws Exception {
        String body = "{\"queue\":\"q\",\"exchange\":\"e\",\"routingKey\":\"a.*\"}";

        mockMvc.perform(post(BASE + "/bindings").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(delete(BASE + "/bindings").with(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNoContent());
    }
}
