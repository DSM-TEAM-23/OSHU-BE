package com.dsm.oshu.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String category;
    @Column(length = 1000) private String description;
    @Column(nullable = false) private String address;
    @Column(nullable = false) private Double latitude;
    @Column(nullable = false) private Double longitude;
    private String phone;
    private String openingHours;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CrowdLevel crowdLevel = CrowdLevel.NORMAL;
    @Column(nullable = false) private Integer estimatedWaitingMinutes = 0;
    @Column(nullable = false) private String ownerLoginId;

    protected Store() {
    }

    public Store(String name, String category, String description, String address, Double latitude,
                 Double longitude, String phone, String openingHours, String ownerLoginId) {
        this.name = name; this.category = category; this.description = description; this.address = address;
        this.latitude = latitude; this.longitude = longitude; this.phone = phone;
        this.openingHours = openingHours; this.ownerLoginId = ownerLoginId;
    }

    public void update(String description, String phone, String openingHours) {
        if (description != null) this.description = description;
        if (phone != null) this.phone = phone;
        if (openingHours != null) this.openingHours = openingHours;
    }

    public void updateCrowd(CrowdLevel crowdLevel, Integer estimatedWaitingMinutes) {
        this.crowdLevel = crowdLevel;
        this.estimatedWaitingMinutes = estimatedWaitingMinutes;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getPhone() { return phone; }
    public String getOpeningHours() { return openingHours; }
    public CrowdLevel getCrowdLevel() { return crowdLevel; }
    public Integer getEstimatedWaitingMinutes() { return estimatedWaitingMinutes; }
    public String getOwnerLoginId() { return ownerLoginId; }
}
