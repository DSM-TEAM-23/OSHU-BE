package com.dsm.oshu.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(@NotBlank String idToken) {
}
