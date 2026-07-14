package com.dsm.oshu.auth.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class GoogleIdTokenVerifier {
    private final JwtDecoder googleIdTokenDecoder;
    private final String webClientId;

    public GoogleIdTokenVerifier(
            @Qualifier("googleIdTokenDecoder") JwtDecoder googleIdTokenDecoder,
            @Value("${oshu.google.web-client-id:}") String webClientId
    ) {
        this.googleIdTokenDecoder = googleIdTokenDecoder;
        this.webClientId = webClientId;
    }

    public GoogleAccount verify(String idToken) {
        if (webClientId.isBlank()) {
            throw new IllegalStateException("GOOGLE_WEB_CLIENT_ID가 설정되지 않았습니다.");
        }

        try {
            Jwt jwt = googleIdTokenDecoder.decode(idToken);
            String subject = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");

            if (subject == null || subject.isBlank() || email == null || email.isBlank()
                    || !Boolean.TRUE.equals(emailVerified)) {
                throw new IllegalArgumentException("유효한 Google 계정 정보가 아닙니다.");
            }
            return new GoogleAccount(subject, email);
        } catch (JwtException exception) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 Google ID 토큰입니다.");
        }
    }

    public record GoogleAccount(String subject, String email) {
    }
}
