package com.medibridge.controller;

import com.medibridge.dto.ApiResponse;
import com.medibridge.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> userDashboard(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("", dashboardService.getUserDashboard(userId)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> adminDashboard() {
        return ResponseEntity.ok(ApiResponse.ok("", dashboardService.getAdminDashboard()));
    }
}
