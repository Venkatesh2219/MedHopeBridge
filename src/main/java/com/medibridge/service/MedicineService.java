package com.medibridge.service;

import com.medibridge.dto.MedicineDTO;
import com.medibridge.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MedicineService {
    MedicineDTO.Response addMedicine(Long userId, MedicineDTO.Request request);
    MedicineDTO.Response editMedicine(Long medicineId, Long userId, MedicineDTO.Request request);
    MedicineDTO.Response getMedicineById(Long id);

    List<MedicineDTO.Response>  getMedicinesByUser(Long userId);
    Page<MedicineDTO.Response>  getMedicinesByUserPaged(Long userId, Pageable pageable);

    List<MedicineDTO.Response>  getAllDonations();
    Page<MedicineDTO.Response>  getAllDonationsPaged(Pageable pageable);

    List<MedicineDTO.Response>  getNearbyDonations(double lat, double lng, double radiusKm);
    List<MedicineDTO.Response>  searchDonations(String query);

    MedicineDTO.Response updateDonationStatus(Long medicineId, Long userId, Medicine.DonationStatus status);
    String uploadMedicineImage(Long medicineId, Long userId, MultipartFile file);
    void deleteMedicine(Long medicineId, Long userId);

    List<MedicineDTO.Response>  getAllMedicines();
    Page<MedicineDTO.Response>  getAllMedicinesPaged(Pageable pageable);
    void adminDeleteMedicine(Long medicineId);

    Map<String, Object> getExpiryReport();
}
