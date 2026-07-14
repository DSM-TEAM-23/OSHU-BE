package com.dsm.oshu.inquiry.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tbl_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "user_name", nullable = false)
    private String name;
    @Column(name = "user_number", nullable = false)
    private String number;

    @Builder
    public Post(String title, String content, String name, String number){
        this.title = title;
        this.content = content;
        this.name = name;
        this.number = number;
    }

    public void update(String title, String content, String name, String number){
        this.title = title;
        this.content = content;
        this.name = name;
        this.number = number;
    }
}

