package com.dsm.oshu.application.dto;

import com.dsm.oshu.domain.store.CrowdLevel;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

public final class OshuDtos {
    private OshuDtos() { }

    public record SignUpRequest(@NotBlank String loginId, @NotBlank String password) { }
    public record LoginRequest(@NotBlank String loginId, @NotBlank String password) { }
    public record TokenResponse(String accessToken, String tokenType) { }
    public record MessageResponse(String message) { }

    public record StoreCreateRequest(@NotBlank String name, @NotBlank String category, String description,
                                     @NotBlank String address, @NotNull Double latitude, @NotNull Double longitude,
                                     String phone, String openingHours) { }
    public record StoreUpdateRequest(String description, String phone, String openingHours) { }
    public record CrowdStatusRequest(@NotNull CrowdLevel level, @NotNull @PositiveOrZero Integer estimatedWaitingMinutes) { }
    public record PromotionRequest(@NotBlank String type, @NotBlank String title, String content, String imageUrl,
                                   @NotNull LocalDateTime startAt, @NotNull @Future LocalDateTime endAt) { }
    public record TimeSaleRequest(@NotBlank String productName, @NotNull @Positive Integer originalPrice,
                                  @NotNull @Positive Integer salePrice, @NotNull LocalDateTime startAt,
                                  @NotNull @Future LocalDateTime endAt, String notice) { }

    public record StoreCardResponse(Long storeId, String name, String category, String address,
                                    Double latitude, Double longitude, String crowdLevel,
                                    boolean timeSaleActive, boolean externalData) { }
    public record StoreDetailResponse(Long storeId, String name, String category, String description, String address,
                                      Double latitude, Double longitude, String phone, String openingHours,
                                      CrowdStatusResponse crowdStatus, List<PromotionResponse> promotions,
                                      List<TimeSaleResponse> timeSales) { }
    public record CrowdStatusResponse(String level, String label, Integer estimatedWaitingMinutes) { }
    public record PromotionResponse(Long promotionId, Long storeId, String storeName, String type, String title,
                                    String content, String imageUrl, LocalDateTime startAt, LocalDateTime endAt,
                                    String status) { }
    public record TimeSaleResponse(Long timeSaleId, Long storeId, String productName, Integer originalPrice,
                                   Integer salePrice, LocalDateTime startAt, LocalDateTime endAt, String notice,
                                   String status) { }
    public record PageResponse<T>(List<T> content, int page, int size, long totalElements) { }
}
