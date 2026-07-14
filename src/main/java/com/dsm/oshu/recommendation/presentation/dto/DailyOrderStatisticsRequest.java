package com.dsm.oshu.recommendation.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record DailyOrderStatisticsRequest(
        @NotNull LocalDate orderDate,
        @NotEmpty @Size(max = 24) List<@Valid HourlyOrderCountRequest> hourlyOrderCounts
) {
}
