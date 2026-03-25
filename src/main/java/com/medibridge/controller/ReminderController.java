package com.medibridge.controller;

import com.medibridge.dto.ApiResponse;
import com.medibridge.dto.ReminderDTO;
import com.medibridge.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReminderDTO.Response>>> getAll(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("", reminderService.getRemindersForUser(userId)));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<ReminderDTO.Response>>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("", reminderService.getUnreadReminders(userId)));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCount(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("",
                Map.of("unreadCount", reminderService.getUnreadCount(userId))));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(@PathVariable Long userId) {
        reminderService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.ok("All marked as read"));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        reminderService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read"));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Void>> generate() {
        reminderService.generateExpiryReminders();
        return ResponseEntity.ok(ApiResponse.ok("Reminders generated"));
    }
}
