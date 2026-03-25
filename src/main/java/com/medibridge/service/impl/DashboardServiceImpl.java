package com.medibridge.service.impl;

import com.medibridge.dto.MedicineDTO;
import com.medibridge.dto.MedicineRequestDTO;
import com.medibridge.model.Medicine;
import com.medibridge.model.MedicineRequest;
import com.medibridge.model.User;
import com.medibridge.repository.*;
import com.medibridge.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository            userRepository;
    private final MedicineRepository        medicineRepository;
    private final MedicineRequestRepository requestRepository;
    private final ReminderRepository        reminderRepository;
    private final DonationHistoryRepository historyRepository;

    @Override
    public Map<String, Object> getUserDashboard(Long userId) {
        LocalDate today    = LocalDate.now();
        LocalDate in30     = today.plusDays(30);
        LocalDate in90     = today.plusDays(90);

        List<Medicine> myMeds       = medicineRepository.findByUserId(userId);
        List<Medicine> critical     = medicineRepository.findExpiringSoon(userId, today, in30);
        List<Medicine> warning      = medicineRepository.findExpiringSoon(userId, in30.plusDays(1), in90);
        long donations              = medicineRepository.countByDonationStatus(Medicine.DonationStatus.AVAILABLE_TO_DONATE);
        long myRequests             = requestRepository.findByUserId(userId).size();
        long unread                 = reminderRepository.countByUserIdAndIsRead(userId, false);
        long donationsMade          = historyRepository.findByDonorIdOrderByCreatedAtDesc(userId).size();
        long requestsFulfilled      = historyRepository.findByReceiverIdOrderByCreatedAtDesc(userId).size();

        List<MedicineDTO.Response> recentDonations = medicineRepository
                .findByDonationStatus(Medicine.DonationStatus.AVAILABLE_TO_DONATE)
                .stream().limit(5).map(MedicineDTO.Response::from).toList();

        List<MedicineRequestDTO.Response> recentRequests = requestRepository
                .findByRequestStatus(MedicineRequest.RequestStatus.OPEN)
                .stream().limit(5).map(MedicineRequestDTO.Response::from).toList();

        Map<String, Object> data = new HashMap<>();
        data.put("totalMyMedicines",   myMeds.size());
        data.put("expiringCritical",   critical.size());
        data.put("expiringWarning",    warning.size());
        data.put("communityDonations", donations);
        data.put("myRequests",         myRequests);
        data.put("unreadReminders",    unread);
        data.put("donationsMade",      donationsMade);
        data.put("requestsFulfilled",  requestsFulfilled);
        data.put("recentDonations",    recentDonations);
        data.put("recentRequests",     recentRequests);
        return data;
    }

    @Override
    public Map<String, Object> getAdminDashboard() {
        LocalDate today = LocalDate.now();

        long totalUsers    = userRepository.count();
        long activeUsers   = userRepository.countByStatus(User.UserStatus.ACTIVE);
        long totalMeds     = medicineRepository.count();
        long donations     = medicineRepository.countByDonationStatus(Medicine.DonationStatus.AVAILABLE_TO_DONATE);
        long openRequests  = requestRepository.countByRequestStatus(MedicineRequest.RequestStatus.OPEN);
        long expiringSoon  = medicineRepository.findAllExpiringSoon(today, today.plusDays(30)).size();
        long expired       = medicineRepository.findAllExpired(today).size();
        long totalHistory  = historyRepository.count();

        List<MedicineDTO.Response> latestMeds = medicineRepository.findAll()
                .stream().sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(6).map(MedicineDTO.Response::from).toList();

        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers",     totalUsers);
        data.put("activeUsers",    activeUsers);
        data.put("totalMedicines", totalMeds);
        data.put("donations",      donations);
        data.put("openRequests",   openRequests);
        data.put("expiringSoon",   expiringSoon);
        data.put("expired",        expired);
        data.put("totalHistory",   totalHistory);
        data.put("latestMedicines",latestMeds);
        return data;
    }
}
