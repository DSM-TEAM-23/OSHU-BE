package com.dsm.oshu.inquiry.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryRequest {
    @NotBlank(message = "문의 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "문의 내용은 필수입니다.")
    private String content;

    @NotBlank(message = "작성자 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "연락처는 필수입니다.")
    private String number;
}
