package com.dsm.oshu.domain.store;

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
@Table(name = "time_sales")
public class TimeSale {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "store_id", nullable = false) private Store store;
    @Column(nullable = false) private String productName;
    @Column(nullable = false) private Integer originalPrice;
    @Column(nullable = false) private Integer salePrice;
    @Column(nullable = false) private LocalDateTime startAt;
    @Column(nullable = false) private LocalDateTime endAt;
    private String notice;
    @Column(nullable = false) private String status = "SCHEDULED";

    protected TimeSale() {
    }

    public TimeSale(Store store, String productName, Integer originalPrice, Integer salePrice,
                    LocalDateTime startAt, LocalDateTime endAt, String notice) {
        this.store = store; this.productName = productName; this.originalPrice = originalPrice;
        this.salePrice = salePrice; this.startAt = startAt; this.endAt = endAt; this.notice = notice;
    }

    public void update(String productName, Integer originalPrice, Integer salePrice,
                       LocalDateTime startAt, LocalDateTime endAt, String notice) {
        if (productName != null) this.productName = productName;
        if (originalPrice != null) this.originalPrice = originalPrice;
        if (salePrice != null) this.salePrice = salePrice;
        if (startAt != null) this.startAt = startAt;
        if (endAt != null) this.endAt = endAt;
        if (notice != null) this.notice = notice;
    }
    public void close() { this.status = "CLOSED"; }

    public Long getId() { return id; }
    public Store getStore() { return store; }
    public String getProductName() { return productName; }
    public Integer getOriginalPrice() { return originalPrice; }
    public Integer getSalePrice() { return salePrice; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public String getNotice() { return notice; }
    public String getStatus() { return status; }
}
