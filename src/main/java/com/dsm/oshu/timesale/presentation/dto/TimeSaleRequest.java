package com.dsm.oshu.timesale.presentation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record TimeSaleRequest(@NotBlank String productName, @NotNull @Positive Integer originalPrice,
                              @NotNull @Positive Integer salePrice, @NotNull LocalDateTime startAt,
                              @NotNull @Future LocalDateTime endAt, String notice) {
}
