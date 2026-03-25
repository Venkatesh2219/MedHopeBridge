package com.medibridge.dto;

import com.medibridge.model.User;
import lombok.Data;
import java.time.LocalDateTime;

public class UserDTO {

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String location;
        private Double latitude;
        private Double longitude;
        private User.Role role;
        private User.UserStatus status;
        private Boolean emailVerified;
        private String profilePicture;
        private LocalDateTime createdAt;
        private int medicineCount;

        public static Response from(User u, int medicineCount) {
            Response r = new Response();
            r.setId(u.getId());
            r.setName(u.getName());
            r.setEmail(u.getEmail());
            r.setPhone(u.getPhone());
            r.setLocation(u.getLocation());
            r.setLatitude(u.getLatitude());
            r.setLongitude(u.getLongitude());
            r.setRole(u.getRole());
            r.setStatus(u.getStatus());
            r.setEmailVerified(u.getEmailVerified());
            r.setProfilePicture(u.getProfilePicture());
            r.setCreatedAt(u.getCreatedAt());
            r.setMedicineCount(medicineCount);
            return r;
        }
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private String phone;
        private String location;
        private Double latitude;
        private Double longitude;
    }
}
