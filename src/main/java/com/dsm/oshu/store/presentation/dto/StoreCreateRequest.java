package com.dsm.oshu.store.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreCreateRequest(@NotBlank String name, @NotBlank String category, String description,
                                 @NotBlank String address, @NotNull Double latitude, @NotNull Double longitude,
                                 String phone, String openingHours) {
}
