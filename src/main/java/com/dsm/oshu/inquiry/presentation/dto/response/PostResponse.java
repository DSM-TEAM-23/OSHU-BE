package com.dsm.oshu.inquiry.presentation.dto.response;

import com.dsm.oshu.inquiry.domain.Post;
import lombok.Getter;

@Getter
public class PostResponse
{
    private Long id;
    private String title;
    private String content;
    private String name;
    private String number;

    public PostResponse(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.name = post.getName();
        this.number = post.getNumber();
    }

}
