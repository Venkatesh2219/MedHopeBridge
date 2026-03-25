package com.medibridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ── Send OTP for Registration ──────────────────────────
    @Async
    public void sendRegistrationOtp(String toEmail, String otp) {
        String subject = "MediBridge – Verify Your Email";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;border:1px solid #e0e0e0;border-radius:12px;overflow:hidden">
              <div style="background:linear-gradient(135deg,#14523d,#2ea87a);padding:32px 24px;text-align:center">
                <h1 style="color:#fff;margin:0;font-size:26px">💊 MediBridge</h1>
                <p style="color:rgba(255,255,255,.8);margin:6px 0 0;font-size:14px">Community Medicine Exchange</p>
              </div>
              <div style="padding:32px 24px">
                <h2 style="color:#0e2a1f;margin:0 0 12px">Verify Your Email</h2>
                <p style="color:#555;line-height:1.6">Use the OTP below to complete your registration. It expires in <strong>10 minutes</strong>.</p>
                <div style="text-align:center;margin:28px 0">
                  <span style="display:inline-block;background:#f0f4f2;border:2px dashed #2ea87a;border-radius:10px;padding:16px 40px;font-size:36px;font-weight:700;color:#14523d;letter-spacing:10px">%s</span>
                </div>
                <p style="color:#888;font-size:13px">If you didn't request this, please ignore this email.</p>
              </div>
              <div style="background:#f9f9f9;padding:16px 24px;text-align:center;border-top:1px solid #eee">
                <p style="color:#aaa;font-size:12px;margin:0">© 2025 MediBridge. All rights reserved.</p>
              </div>
            </div>
            """.formatted(otp);
        sendHtmlEmail(toEmail, subject, html);
    }

    // ── Send OTP for Forgot Password ──────────────────────
    @Async
    public void sendForgotPasswordOtp(String toEmail, String otp) {
        String subject = "MediBridge – Password Reset OTP";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;border:1px solid #e0e0e0;border-radius:12px;overflow:hidden">
              <div style="background:linear-gradient(135deg,#14523d,#2ea87a);padding:32px 24px;text-align:center">
                <h1 style="color:#fff;margin:0;font-size:26px">💊 MediBridge</h1>
              </div>
              <div style="padding:32px 24px">
                <h2 style="color:#0e2a1f;margin:0 0 12px">Reset Your Password</h2>
                <p style="color:#555;line-height:1.6">Use the OTP below to reset your password. It expires in <strong>10 minutes</strong>.</p>
                <div style="text-align:center;margin:28px 0">
                  <span style="display:inline-block;background:#fff1f0;border:2px dashed #e85c3a;border-radius:10px;padding:16px 40px;font-size:36px;font-weight:700;color:#e85c3a;letter-spacing:10px">%s</span>
                </div>
                <p style="color:#888;font-size:13px">If you didn't request a password reset, please ignore this email.</p>
              </div>
            </div>
            """.formatted(otp);
        sendHtmlEmail(toEmail, subject, html);
    }

    // ── Notify donor that someone contacted them ───────────
    @Async
    public void sendContactDonorNotification(String donorEmail, String donorName,
                                              String receiverName, String medicineName,
                                              String message) {
        String subject = "MediBridge – Someone wants your donated medicine";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;border:1px solid #e0e0e0;border-radius:12px;overflow:hidden">
              <div style="background:linear-gradient(135deg,#14523d,#2ea87a);padding:28px 24px;text-align:center">
                <h1 style="color:#fff;margin:0;font-size:24px">💊 MediBridge</h1>
              </div>
              <div style="padding:28px 24px">
                <h2 style="color:#0e2a1f">Hi %s! 👋</h2>
                <p style="color:#555;line-height:1.7"><strong>%s</strong> is interested in your donated medicine <strong>%s</strong>.</p>
                %s
                <p style="color:#555;line-height:1.7;margin-top:16px">Please log in to MediBridge to respond and coordinate the handover.</p>
                <div style="text-align:center;margin:24px 0">
                  <a href="http://127.0.0.1:5500" style="background:#2ea87a;color:#fff;padding:12px 28px;border-radius:8px;text-decoration:none;font-weight:600">Open MediBridge →</a>
                </div>
              </div>
            </div>
            """.formatted(
                donorName, receiverName, medicineName,
                message != null ? "<div style='background:#f0f4f2;border-left:3px solid #2ea87a;padding:12px 16px;border-radius:4px;margin-top:12px;color:#333;font-style:italic'>\"" + message + "\"</div>" : ""
        );
        sendHtmlEmail(donorEmail, subject, html);
    }

    // ── Notify requester that someone can fulfill ──────────
    @Async
    public void sendFulfillRequestNotification(String requesterEmail, String requesterName,
                                                String fulfillerName, String medicineName,
                                                String message) {
        String subject = "MediBridge – Someone can fulfill your medicine request!";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;border:1px solid #e0e0e0;border-radius:12px;overflow:hidden">
              <div style="background:linear-gradient(135deg,#14523d,#2ea87a);padding:28px 24px;text-align:center">
                <h1 style="color:#fff;margin:0;font-size:24px">💊 MediBridge</h1>
              </div>
              <div style="padding:28px 24px">
                <h2 style="color:#0e2a1f">Great news, %s! 🎉</h2>
                <p style="color:#555;line-height:1.7"><strong>%s</strong> can provide <strong>%s</strong> that you requested!</p>
                %s
                <p style="color:#555;line-height:1.7;margin-top:16px">Log in to coordinate the medicine pickup/delivery.</p>
                <div style="text-align:center;margin:24px 0">
                  <a href="http://127.0.0.1:5500" style="background:#2ea87a;color:#fff;padding:12px 28px;border-radius:8px;text-decoration:none;font-weight:600">Open MediBridge →</a>
                </div>
              </div>
            </div>
            """.formatted(
                requesterName, fulfillerName, medicineName,
                message != null ? "<div style='background:#f0f4f2;border-left:3px solid #2ea87a;padding:12px 16px;border-radius:4px;margin-top:12px;color:#333;font-style:italic'>\"" + message + "\"</div>" : ""
        );
        sendHtmlEmail(requesterEmail, subject, html);
    }

    // ── Expiry reminder email ──────────────────────────────
    @Async
    public void sendExpiryReminderEmail(String toEmail, String userName,
                                         String medicineName, long daysLeft) {
        String urgency = daysLeft <= 7 ? "🚨 CRITICAL" : "⚠️ Warning";
        String color   = daysLeft <= 7 ? "#e85c3a" : "#f0a500";
        String subject = "MediBridge – " + urgency + ": " + medicineName + " expiring soon";
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;border:1px solid #e0e0e0;border-radius:12px;overflow:hidden">
              <div style="background:%s;padding:24px;text-align:center">
                <h2 style="color:#fff;margin:0">%s – Expiry Alert</h2>
              </div>
              <div style="padding:28px 24px">
                <p style="color:#555;line-height:1.7">Hi <strong>%s</strong>,</p>
                <p style="color:#555;line-height:1.7">Your medicine <strong>%s</strong> is expiring in <strong>%d day(s)</strong>.</p>
                <p style="color:#555;line-height:1.7">Please consider donating it to the community before it expires, or dispose of it safely.</p>
                <div style="text-align:center;margin:24px 0">
                  <a href="http://127.0.0.1:5500" style="background:#2ea87a;color:#fff;padding:12px 28px;border-radius:8px;text-decoration:none;font-weight:600">Manage Medicines →</a>
                </div>
              </div>
            </div>
            """.formatted(color, urgency, userName, medicineName, daysLeft);
        sendHtmlEmail(toEmail, subject, html);
    }

    // ── Generic HTML email sender ──────────────────────────
    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
