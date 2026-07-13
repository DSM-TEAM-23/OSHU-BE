package com.dsm.oshu.store.presentation.dto;

import com.dsm.oshu.store.domain.CrowdLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CrowdStatusRequest(@NotNull CrowdLevel level,
                                 @NotNull @PositiveOrZero Integer estimatedWaitingMinutes) {
}
