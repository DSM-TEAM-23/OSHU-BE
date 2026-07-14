package com.dsm.oshu.auth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleOAuthFailureHandler implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(GoogleOAuthFailureHandler.class);

    private final String mobileCallbackUri;

    public GoogleOAuthFailureHandler(
            @Value("${oshu.google.mobile-callback-uri:oshu://auth/callback}") String mobileCallbackUri
    ) {
        this.mobileCallbackUri = mobileCallbackUri;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.warn("Google OAuth failed: path={}, type={}, message={}", request.getRequestURI(),
                exception.getClass().getSimpleName(), exception.getMessage());

        String redirectUri = UriComponentsBuilder.fromUriString(mobileCallbackUri)
                .queryParam("error", "google_oauth_failed")
                .build()
                .encode()
                .toUriString();
        response.sendRedirect(redirectUri);
    }
}
