package com.medibridge.service.impl;

import com.medibridge.dto.MedicineDTO;
import com.medibridge.exception.BadRequestException;
import com.medibridge.exception.ResourceNotFoundException;
import com.medibridge.model.Medicine;
import com.medibridge.model.User;
import com.medibridge.repository.MedicineRepository;
import com.medibridge.repository.UserRepository;
import com.medibridge.service.MedicineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final UserRepository     userRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public MedicineDTO.Response addMedicine(Long userId, MedicineDTO.Request req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Medicine medicine = Medicine.builder()
                .name(req.getName())
                .type(req.getType())
                .quantity(req.getQuantity())
                .expiryDate(req.getExpiryDate())
                .donationStatus(req.getDonationStatus() != null ? req.getDonationStatus() : Medicine.DonationStatus.PERSONAL)
                .notes(req.getNotes())
                .imageUrl(req.getImageUrl())
                .reminderDays(req.getReminderDays() != null ? req.getReminderDays() : 30)
                .latitude(req.getLatitude() != null ? req.getLatitude() : user.getLatitude())
                .longitude(req.getLongitude() != null ? req.getLongitude() : user.getLongitude())
                .user(user)
                .build();

        return MedicineDTO.Response.from(medicineRepository.save(medicine));
    }

    @Override
    @Transactional
    public String uploadMedicineImage(Long medicineId, Long userId, MultipartFile file) {
        Medicine medicine = findOrThrow(medicineId);
        if (!medicine.getUser().getId().equals(userId))
            throw new BadRequestException("You do not own this medicine");
        try {
            Path uploadPath = Paths.get(uploadDir + "/medicines");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String ext      = getExtension(file.getOriginalFilename());
            String filename = "med_" + medicineId + "_" + UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), uploadPath.resolve(filename),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            String url = "/uploads/medicines/" + filename;
            medicine.setImageUrl(url);
            medicineRepository.save(medicine);
            return url;
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }

    @Override
    @Transactional
    public MedicineDTO.Response editMedicine(Long medicineId, Long userId, MedicineDTO.Request req) {
        Medicine medicine = findOrThrow(medicineId);
        if (!medicine.getUser().getId().equals(userId))
            throw new BadRequestException("You do not own this medicine");

        if (req.getName()           != null) medicine.setName(req.getName());
        if (req.getType()           != null) medicine.setType(req.getType());
        if (req.getQuantity()       != null) medicine.setQuantity(req.getQuantity());
        if (req.getExpiryDate()     != null) medicine.setExpiryDate(req.getExpiryDate());
        if (req.getDonationStatus() != null) medicine.setDonationStatus(req.getDonationStatus());
        if (req.getNotes()          != null) medicine.setNotes(req.getNotes());
        if (req.getReminderDays()   != null) medicine.setReminderDays(req.getReminderDays());
        if (req.getLatitude()       != null) medicine.setLatitude(req.getLatitude());
        if (req.getLongitude()      != null) medicine.setLongitude(req.getLongitude());

        return MedicineDTO.Response.from(medicineRepository.save(medicine));
    }

    @Override
    public MedicineDTO.Response getMedicineById(Long id) {
        return MedicineDTO.Response.from(findOrThrow(id));
    }

    @Override
    public List<MedicineDTO.Response> getMedicinesByUser(Long userId) {
        return medicineRepository.findByUserId(userId).stream()
                .map(MedicineDTO.Response::from).toList();
    }

    @Override
    public Page<MedicineDTO.Response> getMedicinesByUserPaged(Long userId, Pageable pageable) {
        return medicineRepository.findByUserId(userId, pageable)
                .map(MedicineDTO.Response::from);
    }

    @Override
    public List<MedicineDTO.Response> getAllDonations() {
        return medicineRepository.findByDonationStatus(Medicine.DonationStatus.AVAILABLE_TO_DONATE)
                .stream().map(MedicineDTO.Response::from).toList();
    }

    @Override
    public Page<MedicineDTO.Response> getAllDonationsPaged(Pageable pageable) {
        return medicineRepository.findByDonationStatus(Medicine.DonationStatus.AVAILABLE_TO_DONATE, pageable)
                .map(MedicineDTO.Response::from);
    }

    @Override
    public List<MedicineDTO.Response> getNearbyDonations(double lat, double lng, double radiusKm) {
        double deltaLat = radiusKm / 111.0;
        double deltaLng = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));
        return medicineRepository.findNearbyDonations(
                lat - deltaLat, lat + deltaLat,
                lng - deltaLng, lng + deltaLng)
                .stream().map(MedicineDTO.Response::from).toList();
    }

    @Override
    public List<MedicineDTO.Response> searchDonations(String query) {
        return medicineRepository.searchDonations(query)
                .stream().map(MedicineDTO.Response::from).toList();
    }

    @Override
    @Transactional
    public MedicineDTO.Response updateDonationStatus(Long medicineId, Long userId, Medicine.DonationStatus status) {
        Medicine medicine = findOrThrow(medicineId);
        if (!medicine.getUser().getId().equals(userId))
            throw new BadRequestException("You do not own this medicine");
        medicine.setDonationStatus(status);
        return MedicineDTO.Response.from(medicineRepository.save(medicine));
    }

    @Override
    @Transactional
    public void deleteMedicine(Long medicineId, Long userId) {
        Medicine medicine = findOrThrow(medicineId);
        if (!medicine.getUser().getId().equals(userId))
            throw new BadRequestException("You do not own this medicine");
        medicineRepository.deleteById(medicineId);
    }

    @Override
    public List<MedicineDTO.Response> getAllMedicines() {
        return medicineRepository.findAll().stream()
                .map(MedicineDTO.Response::from).toList();
    }

    @Override
    public Page<MedicineDTO.Response> getAllMedicinesPaged(Pageable pageable) {
        return medicineRepository.findAll(pageable)
                .map(MedicineDTO.Response::from);
    }

    @Override
    @Transactional
    public void adminDeleteMedicine(Long medicineId) {
        if (!medicineRepository.existsById(medicineId))
            throw new ResourceNotFoundException("Medicine not found: " + medicineId);
        medicineRepository.deleteById(medicineId);
    }

    // ── Admin: Expiry Report ───────────────────────────────
    @Override
    public Map<String, Object> getExpiryReport() {
        LocalDate today  = LocalDate.now();
        List<Medicine> expired   = medicineRepository.findAllExpired(today);
        List<Medicine> critical  = medicineRepository.findAllExpiringSoon(today, today.plusDays(7));
        List<Medicine> warning   = medicineRepository.findAllExpiringSoon(today.plusDays(8), today.plusDays(30));
        List<Medicine> upcoming  = medicineRepository.findAllExpiringSoon(today.plusDays(31), today.plusDays(90));

        Map<String, Object> report = new HashMap<>();
        report.put("expired",          expired.stream().map(MedicineDTO.Response::from).toList());
        report.put("expiredCount",      expired.size());
        report.put("critical",          critical.stream().map(MedicineDTO.Response::from).toList());
        report.put("criticalCount",     critical.size());
        report.put("warning",           warning.stream().map(MedicineDTO.Response::from).toList());
        report.put("warningCount",      warning.size());
        report.put("upcoming",          upcoming.stream().map(MedicineDTO.Response::from).toList());
        report.put("upcomingCount",     upcoming.size());
        report.put("generatedAt",       java.time.LocalDateTime.now().toString());
        return report;
    }

    private Medicine findOrThrow(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + id));
    }
}
