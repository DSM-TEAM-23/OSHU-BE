package com.dsm.oshu.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(@NotBlank String loginId, @NotBlank String password) {
}
