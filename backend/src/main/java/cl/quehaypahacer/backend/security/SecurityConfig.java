package cl.quehaypahacer.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Backend actúa como recurso protegido (BFF): valida el JWT emitido por
 * Azure AD (issuer, audience, firma y vigencia) antes de autorizar cualquier
 * endpoint privado.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    // cambios aqui
    @Value("${app.azure.tenant-id:1abed6ed-c70a-4381-b9fe-0a67d2f0745e}")
    private String tenantId;
    // hasta aqui

    @Value("${app.azure.client-id}")
    private String clientId;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                // cambios aqui
                .requestMatchers(HttpMethod.GET, "/api/ingredientes/**").permitAll()
                // hasta aqui
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    /**
     * Azure AD firma con RS256 y publica sus llaves en el issuer OIDC.
     * El decoder por defecto solo valida iss/exp/nbf; se agrega el
     * validador de audience porque la rúbrica exige verificarlo explícitamente.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);

        // cambios aqui
        OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> withIssuer = jwt -> {
            String iss = jwt.getIssuer() != null ? jwt.getIssuer().toString() : "";
            if (iss.contains(tenantId) || iss.equals(issuerUri)) {
                return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
            }
            return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                    new org.springframework.security.oauth2.core.OAuth2Error("invalid_token", "Issuer inválido: " + iss, null)
            );
        };
        OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> withAudience =
                new AudienceValidator(clientId);
        OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> combined =
                new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
                        new org.springframework.security.oauth2.jwt.JwtTimestampValidator(),
                        withIssuer,
                        withAudience
                );
        // hasta aqui

        decoder.setJwtValidator(combined);
        return decoder;
    }

    /**
     * Mapea el claim "roles" (App roles asignados en Entra ID) a authorities
     * con prefijo ROLE_, para poder usar @PreAuthorize("hasRole('Usuario')").
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        AzureAdRolesConverter rolesConverter = new AzureAdRolesConverter();
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Orígenes tomados de app.cors.allowed-origins (env CORS_ALLOWED_ORIGINS),
        // separados por coma. Es compatible con el JWT porque éste viaja en el
        // header Authorization, no como cookie — por lo tanto allowCredentials
        // no es necesario aunque se use "*".
        configuration.setAllowedOriginPatterns(List.of(allowedOrigins.split("\\s*,\\s*")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        // Sin allowCredentials(true) para ser compatible con origin wildcard

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
