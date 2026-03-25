package com.medibridge.service;

import com.medibridge.dto.MedicineRequestDTO;
import com.medibridge.model.MedicineRequest;
import java.util.List;

public interface MedicineRequestService {
    MedicineRequestDTO.Response createRequest(Long userId, MedicineRequestDTO.Request request);
    List<MedicineRequestDTO.Response> getAllOpenRequests();
    List<MedicineRequestDTO.Response> getRequestsByUser(Long userId);
    MedicineRequestDTO.Response updateRequestStatus(Long requestId, Long userId, MedicineRequest.RequestStatus status);
    void deleteRequest(Long requestId, Long userId);
    List<MedicineRequestDTO.Response> getAllRequests();
    void adminDeleteRequest(Long requestId);
}
