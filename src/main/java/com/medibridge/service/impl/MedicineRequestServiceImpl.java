package com.medibridge.service.impl;

import com.medibridge.dto.MedicineRequestDTO;
import com.medibridge.exception.BadRequestException;
import com.medibridge.exception.ResourceNotFoundException;
import com.medibridge.model.MedicineRequest;
import com.medibridge.model.User;
import com.medibridge.repository.MedicineRequestRepository;
import com.medibridge.repository.UserRepository;
import com.medibridge.service.MedicineRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineRequestServiceImpl implements MedicineRequestService {

    private final MedicineRequestRepository requestRepository;
    private final UserRepository            userRepository;

    @Override
    @Transactional
    public MedicineRequestDTO.Response createRequest(Long userId, MedicineRequestDTO.Request req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        MedicineRequest request = MedicineRequest.builder()
                .medicineName(req.getMedicineName()).quantity(req.getQuantity())
                .urgency(req.getUrgency() != null ? req.getUrgency() : MedicineRequest.Urgency.NORMAL)
                .location(req.getLocation()).details(req.getDetails())
                .requestStatus(MedicineRequest.RequestStatus.OPEN).user(user).build();
        return MedicineRequestDTO.Response.from(requestRepository.save(request));
    }

    @Override
    public List<MedicineRequestDTO.Response> getAllOpenRequests() {
        return requestRepository.findByRequestStatus(MedicineRequest.RequestStatus.OPEN)
                .stream().map(MedicineRequestDTO.Response::from).toList();
    }

    @Override
    public List<MedicineRequestDTO.Response> getRequestsByUser(Long userId) {
        return requestRepository.findByUserId(userId)
                .stream().map(MedicineRequestDTO.Response::from).toList();
    }

    @Override
    @Transactional
    public MedicineRequestDTO.Response updateRequestStatus(Long id, Long userId, MedicineRequest.RequestStatus status) {
        MedicineRequest req = findOrThrow(id);
        if (!req.getUser().getId().equals(userId)) throw new BadRequestException("You do not own this request");
        req.setRequestStatus(status);
        return MedicineRequestDTO.Response.from(requestRepository.save(req));
    }

    @Override
    @Transactional
    public void deleteRequest(Long id, Long userId) {
        MedicineRequest req = findOrThrow(id);
        if (!req.getUser().getId().equals(userId)) throw new BadRequestException("You do not own this request");
        requestRepository.deleteById(id);
    }

    @Override
    public List<MedicineRequestDTO.Response> getAllRequests() {
        return requestRepository.findAll().stream().map(MedicineRequestDTO.Response::from).toList();
    }

    @Override
    @Transactional
    public void adminDeleteRequest(Long id) {
        if (!requestRepository.existsById(id)) throw new ResourceNotFoundException("Request not found: " + id);
        requestRepository.deleteById(id);
    }

    private MedicineRequest findOrThrow(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + id));
    }
}
