package com.dsm.oshu.recommendation.domain;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(name = "hourly_order_statistics", uniqueConstraints = @UniqueConstraint(
        name = "uk_hourly_order_statistics_store_date_hour",
        columnNames = {"store_id", "order_date", "order_hour"}))
public class HourlyOrderStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "order_hour", nullable = false)
    private int hour;

    @Column(nullable = false)
    private int orderCount;

    protected HourlyOrderStatistic() {
    }

    public HourlyOrderStatistic(Store store, LocalDate orderDate, int hour, int orderCount) {
        this.store = store;
        this.orderDate = orderDate;
        this.hour = hour;
        this.orderCount = orderCount;
    }

    public void updateOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public int getHour() {
        return hour;
    }

    public int getOrderCount() {
        return orderCount;
    }
}
