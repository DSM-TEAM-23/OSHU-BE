package com.dsm.oshu.auth.service;

import com.dsm.oshu.auth.domain.Account;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Component
public class JwtTokenProvider {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final long accessTokenExpirationSeconds;

    public JwtTokenProvider(@Value("${oshu.jwt.secret}") String secret,
                            @Value("${oshu.jwt.access-token-expiration-seconds:43200}") long accessTokenExpirationSeconds) {
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
    }

    public String createAccessToken(Account account) {
        Instant now = Instant.now();
        List<String> authorities = List.of("ROLE_" + account.getRole().name());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(account.getLoginId())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpirationSeconds))
                .claim("role", account.getRole().name())
                .claim("authorities", authorities)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }

    @SuppressWarnings("unchecked")
    public Collection<String> extractAuthorities(Jwt jwt) {
        Object authorities = jwt.getClaims().get("authorities");
        if (authorities instanceof Collection<?> values) {
            return values.stream().map(String::valueOf).toList();
        }
        Object role = jwt.getClaims().get("role");
        if (role == null) {
            return List.of();
        }
        String roleName = String.valueOf(role);
        return roleName.startsWith("ROLE_") ? List.of(roleName) : List.of("ROLE_" + roleName);
    }
}
