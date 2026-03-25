package com.medibridge.controller;

import com.medibridge.dto.ApiResponse;
import com.medibridge.dto.AuthDTO;
import com.medibridge.model.OtpVerification;
import com.medibridge.service.UserService;
import com.medibridge.service.impl.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService  otpService;

    /** POST /api/auth/send-otp  — Send OTP to email */
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@RequestBody AuthDTO.SendOtpRequest req) {
        OtpVerification.OtpType type = "FORGOT_PASSWORD".equalsIgnoreCase(req.getType())
                ? OtpVerification.OtpType.FORGOT_PASSWORD
                : OtpVerification.OtpType.REGISTRATION;
        otpService.sendOtp(req.getEmail(), type);
        return ResponseEntity.ok(ApiResponse.ok("OTP sent to " + req.getEmail()));
    }

    /** POST /api/auth/verify-otp  — Just verify without registering */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody AuthDTO.VerifyOtpRequest req) {
        OtpVerification.OtpType type = "FORGOT_PASSWORD".equalsIgnoreCase(req.getType())
                ? OtpVerification.OtpType.FORGOT_PASSWORD
                : OtpVerification.OtpType.REGISTRATION;
        otpService.verifyOtp(req.getEmail(), req.getOtp(), type);
        return ResponseEntity.ok(ApiResponse.ok("OTP verified successfully"));
    }

    /** POST /api/auth/register */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> register(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        AuthDTO.AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Account created successfully", response));
    }

    /** POST /api/auth/login */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", userService.login(request)));
    }

    /** POST /api/auth/forgot-password */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody AuthDTO.ForgotPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Password reset successfully"));
    }
}
