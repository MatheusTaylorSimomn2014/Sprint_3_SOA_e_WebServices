package com.ford.loyaltyservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tier_name", length = 20, unique = true, nullable = false)
    private String tierName; 

    @Column(name = "min_points")
    private Integer minPoints;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "warranty_extension_months")
    private Integer warrantyExtensionMonths;

    @Column(name = "free_services_per_year")
    private Integer freeServicesPerYear;

    @Column(name = "priority_support")
    private Boolean prioritySupport;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
}
