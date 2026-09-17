package com.ford.loyaltyservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", unique = true, nullable = false)
    private Long customerId;

    @Column(name = "points")
    private Integer points;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id")
    private LoyaltyTier tier;

    @Column(name = "total_services")
    private Integer totalServices;

    @Column(name = "total_spent")
    private Double totalSpent;

    @Column(name = "last_service_date")
    private LocalDateTime lastServiceDate;

    @Column(name = "member_since")
    private LocalDateTime memberSince;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (points == null) {
            points = 0;
        }
        if (totalServices == null) {
            totalServices = 0;
        }
        if (totalSpent == null) {
            totalSpent = 0.0;
        }
        if (isActive == null) {
            isActive = true;
        }
        if (memberSince == null) {
            memberSince = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
