package com.dsm.oshu.promotion.domain;

import com.dsm.oshu.store.domain.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
public class Promotion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "store_id", nullable = false) private Store store;
    @Column(nullable = false) private String type;
    @Column(nullable = false) private String title;
    @Column(length = 2000) private String content;
    private String imageUrl;
    @Column(nullable = false) private LocalDateTime startAt;
    @Column(nullable = false) private LocalDateTime endAt;
    @Column(nullable = false) private String status = "ACTIVE";

    protected Promotion() {
    }

    public Promotion(Store store, String type, String title, String content, String imageUrl,
                     LocalDateTime startAt, LocalDateTime endAt) {
        this.store = store; this.type = type; this.title = title; this.content = content;
        this.imageUrl = imageUrl; this.startAt = startAt; this.endAt = endAt;
    }

    public void update(String type, String title, String content, String imageUrl,
                       LocalDateTime startAt, LocalDateTime endAt) {
        if (type != null) this.type = type;
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (startAt != null) this.startAt = startAt;
        if (endAt != null) this.endAt = endAt;
    }

    public Long getId() { return id; }
    public Store getStore() { return store; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public String getStatus() { return status; }
}
