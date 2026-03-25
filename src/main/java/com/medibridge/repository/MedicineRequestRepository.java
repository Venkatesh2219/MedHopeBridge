package com.medibridge.repository;

import com.medibridge.model.MedicineRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicineRequestRepository extends JpaRepository<MedicineRequest, Long> {
    List<MedicineRequest> findByUserId(Long userId);
    List<MedicineRequest> findByRequestStatus(MedicineRequest.RequestStatus status);
    List<MedicineRequest> findByUrgency(MedicineRequest.Urgency urgency);
    long countByRequestStatus(MedicineRequest.RequestStatus status);
}
