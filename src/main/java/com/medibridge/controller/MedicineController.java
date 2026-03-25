package com.medibridge.controller;

import com.medibridge.dto.ApiResponse;
import com.medibridge.dto.MedicineDTO;
import com.medibridge.model.Medicine;
import com.medibridge.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    /** POST /api/medicines?userId= */
    @PostMapping
    public ResponseEntity<ApiResponse<MedicineDTO.Response>> add(
            @RequestParam Long userId,
            @Valid @RequestBody MedicineDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Medicine added", medicineService.addMedicine(userId, request)));
    }

    /** PUT /api/medicines/{id}?userId= */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicineDTO.Response>> edit(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody MedicineDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.ok("Medicine updated",
                medicineService.editMedicine(id, userId, request)));
    }

    /** GET /api/medicines/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicineDTO.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("", medicineService.getMedicineById(id)));
    }

    /** GET /api/medicines/user/{userId}?page=0&size=10 */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean paginate) {
        if (paginate) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<MedicineDTO.Response> result = medicineService.getMedicinesByUserPaged(userId, pageable);
            return ResponseEntity.ok(ApiResponse.ok("", result));
        }
        return ResponseEntity.ok(ApiResponse.ok("", medicineService.getMedicinesByUser(userId)));
    }

    /** GET /api/medicines/donations?page=0&size=12 */
    @GetMapping("/donations")
    public ResponseEntity<ApiResponse<?>> getDonations(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "false") boolean paginate) {
        if (paginate) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<MedicineDTO.Response> result = medicineService.getAllDonationsPaged(pageable);
            return ResponseEntity.ok(ApiResponse.ok("", result));
        }
        return ResponseEntity.ok(ApiResponse.ok("", medicineService.getAllDonations()));
    }

    /** GET /api/medicines/search?q=paracetamol */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MedicineDTO.Response>>> search(
            @RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok("", medicineService.searchDonations(q)));
    }

    /** GET /api/medicines/nearby?lat=&lng=&radius= */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<MedicineDTO.Response>>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radius) {
        return ResponseEntity.ok(ApiResponse.ok("", medicineService.getNearbyDonations(lat, lng, radius)));
    }

    /** PATCH /api/medicines/{id}/status?userId=&status= */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MedicineDTO.Response>> updateStatus(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam Medicine.DonationStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated",
                medicineService.updateDonationStatus(id, userId, status)));
    }

    /** POST /api/medicines/{id}/image?userId= */
    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {
        String url = medicineService.uploadMedicineImage(id, userId, file);
        return ResponseEntity.ok(ApiResponse.ok("Image uploaded", Map.of("url", url)));
    }

    /** DELETE /api/medicines/{id}?userId= */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id, @RequestParam Long userId) {
        medicineService.deleteMedicine(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Medicine deleted"));
    }

    /* ── Admin ──────────────────────────────────────────── */

    /** GET /api/medicines/admin/all?page=0&size=15 */
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<?>> adminGetAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "false") boolean paginate) {
        if (paginate) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            return ResponseEntity.ok(ApiResponse.ok("", medicineService.getAllMedicinesPaged(pageable)));
        }
        return ResponseEntity.ok(ApiResponse.ok("", medicineService.getAllMedicines()));
    }

    /** GET /api/medicines/admin/expiry-report */
    @GetMapping("/admin/expiry-report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> expiryReport() {
        return ResponseEntity.ok(ApiResponse.ok("", medicineService.getExpiryReport()));
    }

    /** DELETE /api/medicines/admin/{id} */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> adminDelete(@PathVariable Long id) {
        medicineService.adminDeleteMedicine(id);
        return ResponseEntity.ok(ApiResponse.ok("Medicine removed"));
    }
}
