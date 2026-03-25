package com.medibridge.service.impl;

import com.medibridge.exception.BadRequestException;
import com.medibridge.model.OtpVerification;
import com.medibridge.repository.OtpVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;

    @Value("${app.otp.expiry-minutes:10}")
    private int expiryMinutes;

    // ── Generate and send OTP ──────────────────────────────
    @Transactional
    public void sendOtp(String email, OtpVerification.OtpType type) {
        // Delete old OTPs for this email+type
        otpRepository.deleteByEmailAndOtpType(email, type);

        String otp = generateOtp();

        OtpVerification record = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .otpType(type)
                .used(false)
                .expiresAt(LocalDateTime.now().plusMinutes(expiryMinutes))
                .build();

        otpRepository.save(record);

        // Send via email
        if (type == OtpVerification.OtpType.REGISTRATION) {
            emailService.sendRegistrationOtp(email, otp);
        } else {
            emailService.sendForgotPasswordOtp(email, otp);
        }

        log.info("OTP sent to {} for {}", email, type);
    }

    // ── Verify OTP ─────────────────────────────────────────
    @Transactional
    public void verifyOtp(String email, String otp, OtpVerification.OtpType type) {
        OtpVerification record = otpRepository
                .findTopByEmailAndOtpTypeAndUsedFalseOrderByCreatedAtDesc(email, type)
                .orElseThrow(() -> new BadRequestException("No OTP found. Please request a new one."));

        if (LocalDateTime.now().isAfter(record.getExpiresAt())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        if (!record.getOtp().equals(otp)) {
            throw new BadRequestException("Invalid OTP. Please try again.");
        }

        record.setUsed(true);
        otpRepository.save(record);
        log.info("OTP verified for {} ({})", email, type);
    }

    // ── Private: generate 6-digit OTP ─────────────────────
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int num = 100000 + random.nextInt(900000);
        return String.valueOf(num);
    }
}
