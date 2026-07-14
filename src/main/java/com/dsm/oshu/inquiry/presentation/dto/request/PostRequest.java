package com.dsm.oshu.inquiry.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequest {
    private String title;
    private String content;
    private String name;
    private String number;
}
