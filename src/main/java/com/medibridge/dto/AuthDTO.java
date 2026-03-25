package com.medibridge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDTO {

    @Data
    public static class LoginRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
        private String role;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;
        @Email @NotBlank
        private String email;
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
        private String phone;
        @NotBlank(message = "Location is required")
        private String location;
        private Double latitude;
        private Double longitude;
        private String otp;  // OTP entered by user
    }

    @Data
    public static class SendOtpRequest {
        @Email @NotBlank
        private String email;
        private String type; // REGISTRATION or FORGOT_PASSWORD
    }

    @Data
    public static class VerifyOtpRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String otp;
        private String type;
    }

    @Data
    public static class ForgotPasswordRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String otp;
        @NotBlank @Size(min = 8)
        private String newPassword;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank
        private String currentPassword;
        @NotBlank @Size(min = 8)
        private String newPassword;
    }

    @Data
    public static class AuthResponse {
        private Long id;
        private String name;
        private String email;
        private String role;
        private String location;
        private String token;
        private String profilePicture;
        private String message;

        public AuthResponse(Long id, String name, String email,
                            String role, String location, String token, String profilePicture) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
            this.location = location;
            this.token = token;
            this.profilePicture = profilePicture;
            this.message = "Success";
        }
    }
}
