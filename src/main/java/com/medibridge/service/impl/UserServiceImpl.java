package com.medibridge.service.impl;

import com.medibridge.dto.AuthDTO;
import com.medibridge.dto.UserDTO;
import com.medibridge.exception.BadRequestException;
import com.medibridge.exception.ResourceNotFoundException;
import com.medibridge.model.OtpVerification;
import com.medibridge.model.User;
import com.medibridge.repository.MedicineRepository;
import com.medibridge.repository.UserRepository;
import com.medibridge.service.UserService;
import com.medibridge.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository     userRepository;
    private final MedicineRepository medicineRepository;
    private final JwtUtil            jwtUtil;
    private final OtpService         otpService;
    private final PasswordEncoder    passwordEncoder;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ── Register (requires OTP to be already verified) ────
    @Override
    @Transactional
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered: " + req.getEmail());
        }

        // Verify OTP
        if (req.getOtp() == null || req.getOtp().isBlank()) {
            throw new BadRequestException("OTP is required for registration");
        }
        otpService.verifyOtp(req.getEmail(), req.getOtp(), OtpVerification.OtpType.REGISTRATION);

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))   // BCrypt hash
                .phone(req.getPhone())
                .location(req.getLocation())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .emailVerified(true)
                .role(User.Role.USER)
                .status(User.UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());
        log.info("New user registered: {}", saved.getEmail());
        return new AuthDTO.AuthResponse(saved.getId(), saved.getName(), saved.getEmail(),
                saved.getRole().name(), saved.getLocation(), token, saved.getProfilePicture());
    }

    // ── Login ──────────────────────────────────────────────
    @Override
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest req) {
        // Admin shortcut
        if ("ADMIN".equalsIgnoreCase(req.getRole())) {
            if ("admin@medibridge.com".equals(req.getEmail()) && "admin123".equals(req.getPassword())) {
                String token = jwtUtil.generateToken(0L, req.getEmail(), "ADMIN");
                return new AuthDTO.AuthResponse(0L, "Admin", req.getEmail(), "ADMIN", "System", token, null);
            }
            throw new BadRequestException("Invalid admin credentials");
        }

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with this email"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect password");
        }
        if (user.getStatus() == User.UserStatus.SUSPENDED) {
            throw new BadRequestException("Your account has been suspended. Contact support.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        log.info("User logged in: {}", user.getEmail());
        return new AuthDTO.AuthResponse(user.getId(), user.getName(), user.getEmail(),
                user.getRole().name(), user.getLocation(), token, user.getProfilePicture());
    }

    // ── Forgot Password ────────────────────────────────────
    @Override
    @Transactional
    public void resetPassword(AuthDTO.ForgotPasswordRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        otpService.verifyOtp(req.getEmail(), req.getOtp(), OtpVerification.OtpType.FORGOT_PASSWORD);

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset for: {}", req.getEmail());
    }

    // ── Change Password ────────────────────────────────────
    @Override
    @Transactional
    public void changePassword(Long userId, AuthDTO.ChangePasswordRequest req) {
        User user = findUserOrThrow(userId);
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    // ── Get User ───────────────────────────────────────────
    @Override
    public UserDTO.Response getUserById(Long id) {
        User user = findUserOrThrow(id);
        return UserDTO.Response.from(user, (int) medicineRepository.countByUserId(id));
    }

    // ── Update Profile ─────────────────────────────────────
    @Override
    @Transactional
    public UserDTO.Response updateUser(Long id, UserDTO.UpdateRequest req) {
        User user = findUserOrThrow(id);
        if (req.getName()      != null) user.setName(req.getName());
        if (req.getPhone()     != null) user.setPhone(req.getPhone());
        if (req.getLocation()  != null) user.setLocation(req.getLocation());
        if (req.getLatitude()  != null) user.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) user.setLongitude(req.getLongitude());
        return UserDTO.Response.from(userRepository.save(user),
                (int) medicineRepository.countByUserId(id));
    }

    // ── Upload Profile Picture ─────────────────────────────
    @Override
    @Transactional
    public String uploadProfilePicture(Long userId, MultipartFile file) {
        User user = findUserOrThrow(userId);
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String extension = getExtension(file.getOriginalFilename());
            String filename   = "user_" + userId + "_" + UUID.randomUUID() + extension;
            Path filePath     = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/" + filename;
            user.setProfilePicture(url);
            userRepository.save(user);
            return url;
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    // ── Admin: All Users ───────────────────────────────────
    @Override
    public List<UserDTO.Response> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserDTO.Response.from(u, (int) medicineRepository.countByUserId(u.getId())))
                .toList();
    }

    // ── Admin: Search Users ────────────────────────────────
    @Override
    public List<UserDTO.Response> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(u -> UserDTO.Response.from(u, (int) medicineRepository.countByUserId(u.getId())))
                .toList();
    }

    // ── Admin: Delete User ─────────────────────────────────
    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("User not found: " + id);
        userRepository.deleteById(id);
    }

    // ── Admin: Toggle Status ───────────────────────────────
    @Override
    @Transactional
    public User.UserStatus toggleUserStatus(Long id) {
        User user = findUserOrThrow(id);
        user.setStatus(user.getStatus() == User.UserStatus.ACTIVE
                ? User.UserStatus.SUSPENDED : User.UserStatus.ACTIVE);
        userRepository.save(user);
        return user.getStatus();
    }

    // ── Private helpers ────────────────────────────────────
    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }
}
