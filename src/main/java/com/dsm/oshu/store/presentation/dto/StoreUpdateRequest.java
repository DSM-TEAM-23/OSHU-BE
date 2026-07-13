package com.dsm.oshu.store.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가게 정보 수정 요청")
public record StoreUpdateRequest(
        @Schema(description = "가게 소개", example = "갓 구운 빵과 커피를 판매합니다.")
        String description,
        @Schema(description = "가게 전화번호", example = "042-000-0001")
        String phone,
        @Schema(description = "가게 영업시간. 시작 시간과 마감 시간을 함께 입력합니다.", example = "09:00 - 21:00")
        String openingHours) {
}
