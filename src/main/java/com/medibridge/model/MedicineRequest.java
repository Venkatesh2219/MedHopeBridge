package com.medibridge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medicine_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String medicineName;

    @NotBlank
    private String quantity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Urgency urgency = Urgency.NORMAL;

    @NotBlank
    private String location;

    @Column(length = 500)
    private String details;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RequestStatus requestStatus = RequestStatus.OPEN;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum Urgency       { NORMAL, URGENT, CRITICAL }
    public enum RequestStatus { OPEN, FULFILLED, CLOSED }
}
