package com.medibridge.service.impl;

import com.medibridge.dto.ReminderDTO;
import com.medibridge.exception.ResourceNotFoundException;
import com.medibridge.model.Medicine;
import com.medibridge.model.Reminder;
import com.medibridge.repository.MedicineRepository;
import com.medibridge.repository.ReminderRepository;
import com.medibridge.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final MedicineRepository medicineRepository;
    private final EmailService       emailService;

    @Override
    public List<ReminderDTO.Response> getRemindersForUser(Long userId) {
        return reminderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(ReminderDTO.Response::from).toList();
    }

    @Override
    public List<ReminderDTO.Response> getUnreadReminders(Long userId) {
        return reminderRepository.findByUserIdAndIsRead(userId, false)
                .stream().map(ReminderDTO.Response::from).toList();
    }

    @Override
    public long getUnreadCount(Long userId) {
        return reminderRepository.countByUserIdAndIsRead(userId, false);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Reminder> unread = reminderRepository.findByUserIdAndIsRead(userId, false);
        unread.forEach(r -> r.setIsRead(true));
        reminderRepository.saveAll(unread);
    }

    @Override
    @Transactional
    public void markAsRead(Long reminderId) {
        Reminder r = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found: " + reminderId));
        r.setIsRead(true);
        reminderRepository.save(r);
    }

    @Override
    @Scheduled(cron = "${app.reminder.check-cron}")
    @Transactional
    public void generateExpiryReminders() {
        log.info("Running scheduled expiry reminder check...");
        LocalDate today    = LocalDate.now();
        LocalDate in90days = today.plusDays(90);

        List<Medicine> expiringSoon = medicineRepository.findAllExpiringSoon(today, in90days);
        for (Medicine m : expiringSoon) {
            long daysLeft = today.until(m.getExpiryDate()).getDays();
            int threshold = m.getReminderDays() != null ? m.getReminderDays() : 30;

            if (daysLeft <= 7) {
                createReminderIfAbsent(m, Reminder.ReminderType.EXPIRY_CRITICAL,
                    String.format("🚨 CRITICAL: %s expires in %d day(s)!", m.getName(), daysLeft));
            } else if (daysLeft <= threshold) {
                createReminderIfAbsent(m, Reminder.ReminderType.EXPIRY_WARNING,
                    String.format("⚠️ %s expires on %s (%d days left). Consider donating.", m.getName(), m.getExpiryDate(), daysLeft));
            }
            // Also send email
            emailService.sendExpiryReminderEmail(
                m.getUser().getEmail(), m.getUser().getName(), m.getName(), daysLeft);
        }

        // Expired medicines
        List<Medicine> expired = medicineRepository.findAllExpired(today);
        for (Medicine m : expired) {
            createReminderIfAbsent(m, Reminder.ReminderType.EXPIRED,
                String.format("💀 %s has EXPIRED. Please remove it from your inventory.", m.getName()));
        }
        log.info("Reminder check complete.");
    }

    private void createReminderIfAbsent(Medicine m, Reminder.ReminderType type, String message) {
        boolean exists = reminderRepository.existsByUserIdAndMedicineIdAndReminderType(
                m.getUser().getId(), m.getId(), type);
        if (!exists) {
            reminderRepository.save(Reminder.builder()
                    .user(m.getUser()).medicine(m)
                    .message(message).reminderType(type).isRead(false).build());
        }
    }
}
