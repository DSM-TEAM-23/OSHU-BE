package com.dsm.oshu.store.presentation.dto;

import com.dsm.oshu.store.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "가게 등록 요청")
public record StoreCreateRequest(
        @Schema(description = "가게 이름", example = "오슈 베이커리")
        @NotBlank String name,
        @Schema(description = "가게 업종", example = "카페", allowableValues = {"베이커리", "음식점", "카페", "마트", "시장", "식료품", "기타"})
        @NotNull Category category,
        @Schema(description = "기타 업종명", example = "꽃집")
        String customCategory,
        @Schema(description = "가게 소개", example = "갓 구운 빵과 커피를 판매합니다.")
        String description,
        @Schema(description = "가게 주소", example = "대전광역시 유성구 궁동 123")
        @NotBlank String address,
        @Schema(description = "가게 위도", example = "36.3628")
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @Schema(description = "가게 경도", example = "127.3441")
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        @Schema(description = "가게 전화번호", example = "042-000-0001")
        String phone,
        @Schema(description = "가게 영업시간. 시작 시간과 마감 시간을 함께 입력합니다.", example = "09:00 - 21:00")
        String openingHours) {
}
