package com.ford.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", unique = true, nullable = false)
    private Long customerId;

    @Column(name = "email_enabled")
    private Boolean emailEnabled;

    @Column(name = "sms_enabled")
    private Boolean smsEnabled;

    @Column(name = "push_enabled")
    private Boolean pushEnabled;

    @Column(name = "service_reminders")
    private Boolean serviceReminders;

    @Column(name = "promotional_offers")
    private Boolean promotionalOffers;

    @Column(name = "warranty_alerts")
    private Boolean warrantyAlerts;

    @Column(name = "reminder_frequency", length = 20)
    private String reminderFrequency; 

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (emailEnabled == null) emailEnabled = true;
        if (smsEnabled == null) smsEnabled = false;
        if (pushEnabled == null) pushEnabled = true;
        if (serviceReminders == null) serviceReminders = true;
        if (promotionalOffers == null) promotionalOffers = true;
        if (warrantyAlerts == null) warrantyAlerts = true;
        if (reminderFrequency == null) reminderFrequency = "MONTHLY";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
