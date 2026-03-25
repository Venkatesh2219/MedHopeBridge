package com.medibridge.repository;

import com.medibridge.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Reminder> findByUserIdAndIsRead(Long userId, Boolean isRead);
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
    boolean existsByUserIdAndMedicineIdAndReminderType(Long userId, Long medicineId, Reminder.ReminderType type);
}
