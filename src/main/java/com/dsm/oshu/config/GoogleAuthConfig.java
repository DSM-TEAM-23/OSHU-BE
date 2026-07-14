package com.dsm.oshu.config;

import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class GoogleAuthConfig {
    private static final String GOOGLE_JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final Set<String> GOOGLE_ISSUERS = Set.of(
            "https://accounts.google.com",
            "accounts.google.com"
    );

    @Bean("googleIdTokenDecoder")
    JwtDecoder googleIdTokenDecoder(@Value("${oshu.google.web-client-id:}") String webClientId) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(GOOGLE_JWK_SET_URI).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                issuerValidator(),
                audienceValidator(webClientId)
        ));
        return decoder;
    }

    private OAuth2TokenValidator<Jwt> issuerValidator() {
        return jwt -> GOOGLE_ISSUERS.contains(jwt.getClaimAsString("iss"))
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Google ID 토큰 issuer가 올바르지 않습니다.", null));
    }

    private OAuth2TokenValidator<Jwt> audienceValidator(String webClientId) {
        return jwt -> !webClientId.isBlank() && jwt.getAudience().contains(webClientId)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Google ID 토큰 audience가 올바르지 않습니다.", null));
    }
}
