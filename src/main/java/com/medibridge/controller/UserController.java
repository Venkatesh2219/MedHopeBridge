package com.medibridge.controller;

import com.medibridge.dto.ApiResponse;
import com.medibridge.dto.AuthDTO;
import com.medibridge.dto.UserDTO;
import com.medibridge.model.User;
import com.medibridge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** GET /api/users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO.Response>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("", userService.getUserById(id)));
    }

    /** PUT /api/users/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO.Response>> updateUser(
            @PathVariable Long id, @RequestBody UserDTO.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", userService.updateUser(id, request)));
    }

    /** POST /api/users/{id}/profile-picture */
    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadPicture(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String url = userService.uploadProfilePicture(id, file);
        return ResponseEntity.ok(ApiResponse.ok("Profile picture updated", Map.of("url", url)));
    }

    /** PATCH /api/users/{id}/change-password */
    @PatchMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestBody AuthDTO.ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully"));
    }

    /* ── Admin ─────────────────────────────────────────── */

    /** GET /api/users/admin/all */
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<UserDTO.Response>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("", userService.getAllUsers()));
    }

    /** GET /api/users/admin/search?q= */
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<List<UserDTO.Response>>> searchUsers(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok("", userService.searchUsers(q)));
    }

    /** DELETE /api/users/admin/{id} */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted"));
    }

    /** PATCH /api/users/admin/{id}/toggle-status */
    @PatchMapping("/admin/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Map<String, String>>> toggleStatus(@PathVariable Long id) {
        User.UserStatus status = userService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Status updated", Map.of("status", status.name())));
    }
}
