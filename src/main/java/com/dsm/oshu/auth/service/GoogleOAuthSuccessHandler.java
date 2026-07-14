package com.dsm.oshu.auth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleOAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService authService;
    private final String mobileCallbackUri;

    public GoogleOAuthSuccessHandler(
            AuthService authService,
            @Value("${oshu.google.mobile-callback-uri:oshu://auth/callback}") String mobileCallbackUri
    ) {
        this.authService = authService;
        this.mobileCallbackUri = mobileCallbackUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();
        String subject = googleUser.getAttribute("sub");
        String email = googleUser.getAttribute("email");
        Boolean emailVerified = googleUser.getAttribute("email_verified");

        if (subject == null || subject.isBlank() || email == null || email.isBlank()
                || !Boolean.TRUE.equals(emailVerified)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효한 Google 계정 정보가 아닙니다.");
            return;
        }

        String code = authService.createGoogleLoginCode(subject, email);
        String redirectUri = UriComponentsBuilder.fromUriString(mobileCallbackUri)
                .queryParam("code", code)
                .build()
                .encode()
                .toUriString();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        response.sendRedirect(redirectUri);
    }
}
