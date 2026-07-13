package com.dsm.oshu.web;

import com.dsm.oshu.application.AuthApplicationService;
import com.dsm.oshu.application.dto.OshuDtos.LoginRequest;
import com.dsm.oshu.application.dto.OshuDtos.MessageResponse;
import com.dsm.oshu.application.dto.OshuDtos.SignUpRequest;
import com.dsm.oshu.application.dto.OshuDtos.TokenResponse;
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
    private final AuthApplicationService authApplicationService;
    public AuthController(AuthApplicationService authApplicationService) { this.authApplicationService = authApplicationService; }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public MessageResponse signUp(@Valid @RequestBody SignUpRequest request) { return authApplicationService.signUp(request); }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) { return authApplicationService.login(request); }
}
