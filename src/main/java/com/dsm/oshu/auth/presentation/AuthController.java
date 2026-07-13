package com.dsm.oshu.auth.presentation;

import com.dsm.oshu.auth.service.AuthService;
import com.dsm.oshu.auth.presentation.dto.LoginRequest;
import com.dsm.oshu.auth.presentation.dto.MessageResponse;
import com.dsm.oshu.auth.presentation.dto.SignUpRequest;
import com.dsm.oshu.auth.presentation.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "회원가입 및 로그인")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authApplicationService;
    public AuthController(AuthService authApplicationService) { this.authApplicationService = authApplicationService; }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public MessageResponse signUp(@Valid @RequestBody SignUpRequest request) { return authApplicationService.signUp(request); }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) { return authApplicationService.login(request); }
}
