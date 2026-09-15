package cl.quehaypahacer.backend.controller;

import cl.quehaypahacer.backend.security.SecurityConfig;
import cl.quehaypahacer.backend.service.DespensaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DespensaController.class)
@Import(SecurityConfig.class)
class DespensaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DespensaService despensaService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void sinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/despensa"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conTokenValidoDevuelve200() throws Exception {
        when(despensaService.obtenerDespensa(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/despensa").with(jwt()))
                .andExpect(status().isOk());
    }
}
