package com.medibridge.model;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Builder.Default
    private Boolean expiryReminders = true;

    @Builder.Default
    private Boolean nearbyDonations = true;

    @Builder.Default
    private Boolean requestUpdates = true;

    @Builder.Default
    private Boolean communityUpdates = false;

    @Builder.Default
    private Integer reminderLeadDays = 30;
}
