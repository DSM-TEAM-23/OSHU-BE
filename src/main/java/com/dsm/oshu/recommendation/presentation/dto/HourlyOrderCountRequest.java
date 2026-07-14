package com.dsm.oshu.recommendation.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record HourlyOrderCountRequest(
        @Min(0) @Max(23) int hour,
        @Min(0) int orderCount
) {
}
