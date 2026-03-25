package com.medibridge.service.impl;

import com.medibridge.dto.DonationHistoryDTO;
import com.medibridge.exception.ResourceNotFoundException;
import com.medibridge.model.DonationHistory;
import com.medibridge.model.Medicine;
import com.medibridge.model.MedicineRequest;
import com.medibridge.model.User;
import com.medibridge.repository.DonationHistoryRepository;
import com.medibridge.repository.MedicineRepository;
import com.medibridge.repository.MedicineRequestRepository;
import com.medibridge.repository.UserRepository;
import com.medibridge.service.DonationHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationHistoryServiceImpl implements DonationHistoryService {

    private final DonationHistoryRepository historyRepository;
    private final MedicineRepository        medicineRepository;
    private final MedicineRequestRepository requestRepository;
    private final UserRepository            userRepository;
    private final EmailService              emailService;

    // ── Contact Donor ──────────────────────────────────────
    @Override
    @Transactional
    public DonationHistoryDTO.Response contactDonor(Long receiverId,
                                                     DonationHistoryDTO.ContactRequest req) {
        Medicine medicine = medicineRepository.findById(req.getMedicineId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User donor = medicine.getUser();

        DonationHistory history = DonationHistory.builder()
                .donor(donor)
                .receiver(receiver)
                .medicine(medicine)
                .medicineName(medicine.getName())
                .quantity(medicine.getQuantity())
                .message(req.getMessage())
                .status(DonationHistory.DonationStatus.CONTACTED)
                .build();

        DonationHistory saved = historyRepository.save(history);

        // Send email notification to donor
        if (donor.getEmail() != null) {
            emailService.sendContactDonorNotification(
                    donor.getEmail(), donor.getName(),
                    receiver.getName(), medicine.getName(), req.getMessage());
        }

        log.info("Receiver {} contacted donor {} for medicine {}", receiverId, donor.getId(), medicine.getName());
        return DonationHistoryDTO.Response.from(saved);
    }

    // ── Fulfill Request ────────────────────────────────────
    @Override
    @Transactional
    public DonationHistoryDTO.Response fulfillRequest(Long donorId,
                                                       DonationHistoryDTO.FulfillRequest req) {
        MedicineRequest request = requestRepository.findById(req.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        User donor = userRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User requester = request.getUser();

        DonationHistory history = DonationHistory.builder()
                .donor(donor)
                .receiver(requester)
                .medicineName(request.getMedicineName())
                .quantity(request.getQuantity())
                .message(req.getMessage())
                .status(DonationHistory.DonationStatus.CONTACTED)
                .build();

        DonationHistory saved = historyRepository.save(history);

        // Update request status to fulfilled
        request.setRequestStatus(MedicineRequest.RequestStatus.FULFILLED);
        requestRepository.save(request);

        // Send email notification to requester
        if (requester.getEmail() != null) {
            emailService.sendFulfillRequestNotification(
                    requester.getEmail(), requester.getName(),
                    donor.getName(), request.getMedicineName(), req.getMessage());
        }

        log.info("Donor {} fulfilled request {} for {}", donorId, req.getRequestId(), requester.getName());
        return DonationHistoryDTO.Response.from(saved);
    }

    // ── Get History for User ───────────────────────────────
    @Override
    public List<DonationHistoryDTO.Response> getHistoryForUser(Long userId) {
        return historyRepository
                .findByDonorIdOrReceiverIdOrderByCreatedAtDesc(userId, userId)
                .stream().map(DonationHistoryDTO.Response::from).toList();
    }

    @Override
    public List<DonationHistoryDTO.Response> getDonationsMadeByUser(Long userId) {
        return historyRepository.findByDonorIdOrderByCreatedAtDesc(userId)
                .stream().map(DonationHistoryDTO.Response::from).toList();
    }

    @Override
    public List<DonationHistoryDTO.Response> getRequestsFulfilledForUser(Long userId) {
        return historyRepository.findByReceiverIdOrderByCreatedAtDesc(userId)
                .stream().map(DonationHistoryDTO.Response::from).toList();
    }
}
