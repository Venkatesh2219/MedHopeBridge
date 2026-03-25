package com.medibridge.dto;

import com.medibridge.model.Medicine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MedicineDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Medicine name is required")
        private String name;
        @NotNull(message = "Medicine type is required")
        private Medicine.MedicineType type;
        @NotBlank(message = "Quantity is required")
        private String quantity;
        @NotNull(message = "Expiry date is required")
        private LocalDate expiryDate;
        private Medicine.DonationStatus donationStatus = Medicine.DonationStatus.PERSONAL;
        private String notes;
        private String imageUrl;
        private Integer reminderDays = 30;
        private Double latitude;
        private Double longitude;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private Medicine.MedicineType type;
        private String quantity;
        private LocalDate expiryDate;
        private Medicine.DonationStatus donationStatus;
        private String notes;
        private String imageUrl;
        private Integer reminderDays;
        private Double latitude;
        private Double longitude;
        private LocalDateTime createdAt;
        private Long userId;
        private String userName;
        private String userPhone;
        private String userLocation;
        private String userProfilePicture;

        public static Response from(Medicine m) {
            Response r = new Response();
            r.setId(m.getId());
            r.setName(m.getName());
            r.setType(m.getType());
            r.setQuantity(m.getQuantity());
            r.setExpiryDate(m.getExpiryDate());
            r.setDonationStatus(m.getDonationStatus());
            r.setNotes(m.getNotes());
            r.setImageUrl(m.getImageUrl());
            r.setReminderDays(m.getReminderDays());
            r.setLatitude(m.getLatitude());
            r.setLongitude(m.getLongitude());
            r.setCreatedAt(m.getCreatedAt());
            if (m.getUser() != null) {
                r.setUserId(m.getUser().getId());
                r.setUserName(m.getUser().getName());
                r.setUserPhone(m.getUser().getPhone());
                r.setUserLocation(m.getUser().getLocation());
                r.setUserProfilePicture(m.getUser().getProfilePicture());
            }
            return r;
        }
    }
}
