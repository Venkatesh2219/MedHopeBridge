package com.medibridge.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "donation_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id")
    private User donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    private String medicineName;
    private String quantity;
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DonationStatus status = DonationStatus.CONTACTED;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum DonationStatus { CONTACTED, CONFIRMED, COMPLETED, CANCELLED }
}
