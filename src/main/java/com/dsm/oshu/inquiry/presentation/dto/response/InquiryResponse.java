package com.dsm.oshu.inquiry.presentation.dto.response;

import com.dsm.oshu.inquiry.domain.Inquiry;
import lombok.Getter;

@Getter
public class InquiryResponse
{
    private Long id;
    private String title;
    private String content;
    private String name;
    private String number;

    public InquiryResponse(Inquiry inquiry){
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.name = inquiry.getName();
        this.number = inquiry.getNumber();
    }

}
