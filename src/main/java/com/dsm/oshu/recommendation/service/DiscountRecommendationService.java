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
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public DiscountRecommendationResponse recommend(String ownerLoginId, Long storeId) {
        storeReader.requireOwnedStore(ownerLoginId, storeId);
        LocalDate endDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate startDate = endDate.minusWeeks(4).plusDays(1);
        List<HourlyOrderStatistic> statistics = hourlyOrderStatistics
                .findByStoreIdAndOrderDateBetweenOrderByOrderDateAscHourAsc(storeId, startDate, endDate);
        if (statistics.isEmpty()) {
            throw new IllegalArgumentException("최근 4주 주문 데이터가 없어 할인 시간대를 추천할 수 없습니다.");
        }

        LocalDate analysisStart = statistics.get(0).getOrderDate();
        LocalDate analysisEnd = statistics.get(statistics.size() - 1).getOrderDate();
        int analyzedDays = (int) statistics.stream().map(HourlyOrderStatistic::getOrderDate).distinct().count();
        String analysisJson = createAnalysisJson(statistics, analysisStart, analysisEnd, analyzedDays);
        AiDiscountRecommendation recommendation = claudeClient.recommend(analysisJson);
        validateRecommendation(recommendation);
        return new DiscountRecommendationResponse(recommendation.recommendedDay(), recommendation.startHour(),
                recommendation.endHour(), recommendation.discountRate(), recommendation.reason(), analysisStart,
                analysisEnd, analyzedDays);
    }

    private String createAnalysisJson(List<HourlyOrderStatistic> statistics, LocalDate startDate,
                                      LocalDate endDate, int analyzedDays) {
        Map<TimeBucket, IntSummaryStatistics> summaries = statistics.stream().collect(Collectors.groupingBy(
                statistic -> new TimeBucket(statistic.getOrderDate().getDayOfWeek(), statistic.getHour()),
                LinkedHashMap::new, Collectors.summarizingInt(HourlyOrderStatistic::getOrderCount)));
        List<Map<String, Object>> hourlyAverages = summaries.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<TimeBucket, IntSummaryStatistics> entry) -> entry.getKey().day)
                        .thenComparing(entry -> entry.getKey().hour))
                .map(entry -> Map.<String, Object>of(
                        "day", koreanDay(entry.getKey().day),
                        "hour", entry.getKey().hour,
                        "averageOrderCount", Math.round(entry.getValue().getAverage() * 100.0) / 100.0,
                        "observations", entry.getValue().getCount()))
                .toList();
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "analysisStartDate", startDate,
                    "analysisEndDate", endDate,
                    "analyzedDays", analyzedDays,
                    "hourlyAverages", hourlyAverages));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("주문 통계를 분석할 수 없습니다.", exception);
        }
    }

    private void validateRecommendation(AiDiscountRecommendation recommendation) {
        if (recommendation.startHour() >= recommendation.endHour()) {
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

    private record TimeBucket(DayOfWeek day, int hour) {
    }
}
