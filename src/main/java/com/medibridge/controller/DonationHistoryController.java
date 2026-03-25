package com.medibridge.controller;

import com.medibridge.dto.ApiResponse;
import com.medibridge.dto.DonationHistoryDTO;
import com.medibridge.service.DonationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class DonationHistoryController {

    private final DonationHistoryService historyService;

    /** POST /api/history/contact  — Contact a donor about their medicine */
    @PostMapping("/contact")
    public ResponseEntity<ApiResponse<DonationHistoryDTO.Response>> contactDonor(
            @RequestParam Long userId,
            @RequestBody DonationHistoryDTO.ContactRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Contact request sent",
                historyService.contactDonor(userId, request)));
    }

    /** POST /api/history/fulfill  — Fulfill a medicine request */
    @PostMapping("/fulfill")
    public ResponseEntity<ApiResponse<DonationHistoryDTO.Response>> fulfillRequest(
            @RequestParam Long userId,
            @RequestBody DonationHistoryDTO.FulfillRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Request fulfilled",
                historyService.fulfillRequest(userId, request)));
    }

    /** GET /api/history/user/{userId}  — Full history (donated + received) */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<DonationHistoryDTO.Response>>> getHistory(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("", historyService.getHistoryForUser(userId)));
    }

    /** GET /api/history/user/{userId}/donated  — Only donations made */
    @GetMapping("/user/{userId}/donated")
    public ResponseEntity<ApiResponse<List<DonationHistoryDTO.Response>>> getDonated(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("", historyService.getDonationsMadeByUser(userId)));
    }

    /** GET /api/history/user/{userId}/received  — Only medicines received */
    @GetMapping("/user/{userId}/received")
    public ResponseEntity<ApiResponse<List<DonationHistoryDTO.Response>>> getReceived(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("", historyService.getRequestsFulfilledForUser(userId)));
    }
}
