package com.medibridge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    private MedicineType type;

    @NotBlank
    private String quantity;

    @NotNull
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DonationStatus donationStatus = DonationStatus.PERSONAL;

    private String notes;
    private String imageUrl;        // uploaded medicine photo

    @Builder.Default
    private Integer reminderDays = 30;

    private Double latitude;
    private Double longitude;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum MedicineType   { TABLET, SYRUP, INJECTION, CAPSULE, CREAM, OTHER }
    public enum DonationStatus { PERSONAL, AVAILABLE_TO_DONATE }
}
