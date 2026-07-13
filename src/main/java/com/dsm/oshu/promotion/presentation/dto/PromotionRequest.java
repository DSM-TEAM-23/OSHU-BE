package com.dsm.oshu.promotion.presentation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PromotionRequest(@NotBlank String type, @NotBlank String title, String content, String imageUrl,
                               @NotNull LocalDateTime startAt, @NotNull @Future LocalDateTime endAt) {
}
