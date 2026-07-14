package com.dsm.oshu.recommendation.service;

import com.dsm.oshu.recommendation.domain.HourlyOrderStatistic;
import com.dsm.oshu.recommendation.domain.HourlyOrderStatisticRepository;
import com.dsm.oshu.recommendation.presentation.dto.DailyOrderStatisticsRequest;
import com.dsm.oshu.recommendation.presentation.dto.DiscountRecommendationResponse;
import com.dsm.oshu.recommendation.presentation.dto.HourlyOrderCountRequest;
import com.dsm.oshu.store.domain.Store;
import com.dsm.oshu.store.service.StoreReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DiscountRecommendationService {
    private final HourlyOrderStatisticRepository hourlyOrderStatistics;
    private final StoreReader storeReader;
    private final ClaudeDiscountRecommendationClient claudeClient;
    private final ObjectMapper objectMapper;

    public DiscountRecommendationService(HourlyOrderStatisticRepository hourlyOrderStatistics, StoreReader storeReader,
                                         ClaudeDiscountRecommendationClient claudeClient, ObjectMapper objectMapper) {
        this.hourlyOrderStatistics = hourlyOrderStatistics;
        this.storeReader = storeReader;
        this.claudeClient = claudeClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void saveDailyStatistics(String ownerLoginId, Long storeId, DailyOrderStatisticsRequest request) {
        Store store = storeReader.requireOwnedStore(ownerLoginId, storeId);
        Set<Integer> hours = request.hourlyOrderCounts().stream().map(HourlyOrderCountRequest::hour)
                .collect(Collectors.toSet());
        if (hours.size() != request.hourlyOrderCounts().size()) {
            throw new IllegalArgumentException("같은 시간대의 주문량은 한 번만 전송할 수 있습니다.");
        }

        for (HourlyOrderCountRequest hourlyCount : request.hourlyOrderCounts()) {
            hourlyOrderStatistics.findByStoreIdAndOrderDateAndHour(storeId, request.orderDate(), hourlyCount.hour())
                    .ifPresentOrElse(statistic -> statistic.updateOrderCount(hourlyCount.orderCount()),
                            () -> hourlyOrderStatistics.save(new HourlyOrderStatistic(
                                    store, request.orderDate(), hourlyCount.hour(), hourlyCount.orderCount())));
        }
    }

    public DiscountRecommendationResponse recommend(String ownerLoginId, Long storeId, LocalDate orderDate) {
        storeReader.requireOwnedStore(ownerLoginId, storeId);
        LocalDate analysisDate = orderDate == null ? LocalDate.now(ZoneId.of("Asia/Seoul")) : orderDate;
        List<HourlyOrderStatistic> statistics = hourlyOrderStatistics.findByStoreIdAndOrderDateOrderByHourAsc(storeId,
                analysisDate);
        if (statistics.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜의 주문 데이터가 없어 할인 시간대를 추천할 수 없습니다.");
        }

        String analysisJson = createAnalysisJson(statistics, analysisDate);
        AiDiscountRecommendation recommendation = claudeClient.recommend(analysisJson);
        validateRecommendation(recommendation, analysisDate);
        return new DiscountRecommendationResponse(recommendation.recommendedDay(), recommendation.startHour(),
                recommendation.endHour(), recommendation.discountRate(), recommendation.reason(), analysisDate,
                statistics.size());
    }

    private String createAnalysisJson(List<HourlyOrderStatistic> statistics, LocalDate analysisDate) {
        List<java.util.Map<String, Object>> hourlyOrderCounts = statistics.stream()
                .map(statistic -> java.util.Map.<String, Object>of(
                        "hour", statistic.getHour(),
                        "orderCount", statistic.getOrderCount()))
                .toList();
        try {
            return objectMapper.writeValueAsString(java.util.Map.of(
                    "analysisDate", analysisDate,
                    "analysisDay", koreanDay(analysisDate.getDayOfWeek()),
                    "hourlyOrderCounts", hourlyOrderCounts));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("주문 통계를 분석할 수 없습니다.", exception);
        }
    }

    private void validateRecommendation(AiDiscountRecommendation recommendation, LocalDate analysisDate) {
        if (recommendation.startHour() >= recommendation.endHour()
                || !koreanDay(analysisDate.getDayOfWeek()).equals(recommendation.recommendedDay())) {
            throw new IllegalStateException("AI가 유효하지 않은 할인 시간대를 반환했습니다.");
        }
    }

    private String koreanDay(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "월요일";
            case TUESDAY -> "화요일";
            case WEDNESDAY -> "수요일";
            case THURSDAY -> "목요일";
            case FRIDAY -> "금요일";
            case SATURDAY -> "토요일";
            case SUNDAY -> "일요일";
        };
    }

}
