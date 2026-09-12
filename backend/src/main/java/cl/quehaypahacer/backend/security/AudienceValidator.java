package cl.quehaypahacer.backend.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Verifica que el token haya sido emitido para ESTA aplicación
 * (aud == client id de la App Registration), no para otra API distinta.
 */
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String expectedAudience;

    public AudienceValidator(String expectedAudience) {
        this.expectedAudience = expectedAudience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        // cambios aqui
        if (jwt.getAudience() != null) {
            for (String aud : jwt.getAudience()) {
                if (aud.equals(expectedAudience) || aud.equals("api://" + expectedAudience) || aud.contains(expectedAudience)) {
                    return OAuth2TokenValidatorResult.success();
                }
            }
        }
        // hasta aqui
        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "El token no fue emitido para esta API (audience inválido)",
                null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}
