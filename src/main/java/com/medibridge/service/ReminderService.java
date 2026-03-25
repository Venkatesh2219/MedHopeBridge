package com.medibridge.service;

import com.medibridge.dto.ReminderDTO;
import java.util.List;

public interface ReminderService {
    List<ReminderDTO.Response> getRemindersForUser(Long userId);
    List<ReminderDTO.Response> getUnreadReminders(Long userId);
    long getUnreadCount(Long userId);
    void markAllAsRead(Long userId);
    void markAsRead(Long reminderId);
    void generateExpiryReminders();
}
