package com.medibridge.controller;

import com.medibridge.dto.ApiResponse;
import com.medibridge.dto.MedicineRequestDTO;
import com.medibridge.model.MedicineRequest;
import com.medibridge.service.MedicineRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class MedicineRequestController {

    private final MedicineRequestService requestService;

    @PostMapping
    public ResponseEntity<ApiResponse<MedicineRequestDTO.Response>> create(
            @RequestParam Long userId, @Valid @RequestBody MedicineRequestDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Request posted", requestService.createRequest(userId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicineRequestDTO.Response>>> getOpen() {
        return ResponseEntity.ok(ApiResponse.ok("", requestService.getAllOpenRequests()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<MedicineRequestDTO.Response>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("", requestService.getRequestsByUser(userId)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MedicineRequestDTO.Response>> updateStatus(
            @PathVariable Long id, @RequestParam Long userId,
            @RequestParam MedicineRequest.RequestStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated",
                requestService.updateRequestStatus(id, userId, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id, @RequestParam Long userId) {
        requestService.deleteRequest(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Request deleted"));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<MedicineRequestDTO.Response>>> adminGetAll() {
        return ResponseEntity.ok(ApiResponse.ok("", requestService.getAllRequests()));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> adminDelete(@PathVariable Long id) {
        requestService.adminDeleteRequest(id);
        return ResponseEntity.ok(ApiResponse.ok("Request removed"));
    }
}
