package com.medibridge.repository;

import com.medibridge.model.DonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonationHistoryRepository extends JpaRepository<DonationHistory, Long> {
    List<DonationHistory> findByDonorIdOrderByCreatedAtDesc(Long donorId);
    List<DonationHistory> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<DonationHistory> findByDonorIdOrReceiverIdOrderByCreatedAtDesc(Long donorId, Long receiverId);
}
