package com.dsm.oshu.common.service;

import com.dsm.oshu.timesale.presentation.dto.TimeSaleRequest;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class PeriodValidator {
    public void validatePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
        }
    }

    public void validateTimeSale(TimeSaleRequest request) {
        validatePeriod(request.startAt(), request.endAt());
        if (request.salePrice() >= request.originalPrice()) {
            throw new IllegalArgumentException("할인가는 정상가보다 낮아야 합니다.");
        }
    }
}
