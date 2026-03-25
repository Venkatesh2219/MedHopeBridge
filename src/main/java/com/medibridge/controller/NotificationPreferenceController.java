package com.medibridge.controller;

import com.medibridge.dto.ApiResponse;
import com.medibridge.dto.NotificationPreferenceDTO;
import com.medibridge.model.NotificationPreference;
import com.medibridge.model.User;
import com.medibridge.repository.NotificationPreferenceRepository;
import com.medibridge.repository.UserRepository;
import com.medibridge.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceRepository prefRepository;
    private final UserRepository                   userRepository;

    /** GET /api/preferences/{userId} */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO.Response>> get(
            @PathVariable Long userId) {
        NotificationPreference pref = prefRepository.findByUserId(userId)
                .orElseGet(() -> createDefault(userId));
        return ResponseEntity.ok(ApiResponse.ok("", NotificationPreferenceDTO.Response.from(pref)));
    }

    /** PUT /api/preferences/{userId} */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO.Response>> save(
            @PathVariable Long userId,
            @RequestBody NotificationPreferenceDTO.Request req) {

        NotificationPreference pref = prefRepository.findByUserId(userId)
                .orElseGet(() -> createDefault(userId));

        if (req.getExpiryReminders()  != null) pref.setExpiryReminders(req.getExpiryReminders());
        if (req.getNearbyDonations()  != null) pref.setNearbyDonations(req.getNearbyDonations());
        if (req.getRequestUpdates()   != null) pref.setRequestUpdates(req.getRequestUpdates());
        if (req.getCommunityUpdates() != null) pref.setCommunityUpdates(req.getCommunityUpdates());
        if (req.getReminderLeadDays() != null) pref.setReminderLeadDays(req.getReminderLeadDays());

        NotificationPreference saved = prefRepository.save(pref);
        return ResponseEntity.ok(ApiResponse.ok("Preferences saved", NotificationPreferenceDTO.Response.from(saved)));
    }

    private NotificationPreference createDefault(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        NotificationPreference pref = NotificationPreference.builder()
                .user(user).build();
        return prefRepository.save(pref);
    }
}
