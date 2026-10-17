package cl.quehaypahacer.backend.admin.rabbitmq.controller;

import cl.quehaypahacer.backend.config.rabbitmq.RabbitMQProperties;
import cl.quehaypahacer.backend.messaging.publisher.NotificacionEventPublisher;
import cl.quehaypahacer.backend.messaging.publisher.TicketEventPublisher;
import cl.quehaypahacer.backend.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RabbitTestController.class)
@Import(SecurityConfig.class)
class RabbitTestControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketEventPublisher ticketEventPublisher;

    @MockitoBean
    private NotificacionEventPublisher notificacionEventPublisher;

    @MockitoBean
    private RabbitMQProperties props;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        RabbitMQProperties.RoutingKeys rk = new RabbitMQProperties.RoutingKeys(
                "recetas.ticket.generar",
                "recetas.despensa.notificar",
                "recetas.ticket.dlq",
                "recetas.despensa.dlq"
        );
        when(props.routingKeys()).thenReturn(rk);
    }

    @Test
    void sinTokenDevuelve401() throws Exception {
        mockMvc.perform(post("/api/admin/rabbitmq/test/ticket-valido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conTokenSinRolAdminDevuelve403() throws Exception {
        mockMvc.perform(post("/api/admin/rabbitmq/test/ticket-valido")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Usuario"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void ticketValidoConRolAdminDevuelve202YPublica() throws Exception {
        mockMvc.perform(post("/api/admin/rabbitmq/test/ticket-valido")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Admin"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje").value("Evento válido publicado"))
                .andExpect(jsonPath("$.routingKey").value("recetas.ticket.generar"));

        verify(ticketEventPublisher).publicarTicketCocina(any());
    }

    @Test
    void ticketInvalidoConRolAdminDevuelve202YPublica() throws Exception {
        mockMvc.perform(post("/api/admin/rabbitmq/test/ticket-invalido")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Admin"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje").value("Evento inválido publicado"))
                .andExpect(jsonPath("$.routingKey").value("recetas.ticket.generar"));

        verify(ticketEventPublisher).publicarTicketCocina(any());
    }

    @Test
    void notificacionInvalidaConRolAdminDevuelve202YPublica() throws Exception {
        mockMvc.perform(post("/api/admin/rabbitmq/test/notificacion-invalida")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Admin"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje").value("Notificación inválida publicada"))
                .andExpect(jsonPath("$.routingKey").value("recetas.despensa.notificar"));

        verify(notificacionEventPublisher).publicarNotificacionDespensa(any());
    }
}
