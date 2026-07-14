package com.dsm.oshu.inquiry.domain;


import com.dsm.oshu.store.domain.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tbl_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "user_number", nullable = false)
    private String number;


    @Builder
    public Inquiry(Store store, String title, String content, String name, String number){
        this.store = store;
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
