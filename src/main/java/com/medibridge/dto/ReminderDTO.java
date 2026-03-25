package com.medibridge.dto;

import com.medibridge.model.Reminder;
import lombok.Data;
import java.time.LocalDateTime;

public class ReminderDTO {

    @Data
    public static class Response {
        private Long id;
        private String message;
        private Reminder.ReminderType reminderType;
        private Boolean isRead;
        private LocalDateTime createdAt;
        private Long medicineId;
        private String medicineName;

        public static Response from(Reminder r) {
            Response dto = new Response();
            dto.setId(r.getId());
            dto.setMessage(r.getMessage());
            dto.setReminderType(r.getReminderType());
            dto.setIsRead(r.getIsRead());
            dto.setCreatedAt(r.getCreatedAt());
            if (r.getMedicine() != null) {
                dto.setMedicineId(r.getMedicine().getId());
                dto.setMedicineName(r.getMedicine().getName());
            }
            return dto;
        }
    }
}
