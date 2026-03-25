package com.medibridge.dto;

import com.medibridge.model.MedicineRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

public class MedicineRequestDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Medicine name is required")
        private String medicineName;
        @NotBlank(message = "Quantity is required")
        private String quantity;
        private MedicineRequest.Urgency urgency = MedicineRequest.Urgency.NORMAL;
        @NotBlank(message = "Location is required")
        private String location;
        private String details;
    }

    @Data
    public static class Response {
        private Long id;
        private String medicineName;
        private String quantity;
        private MedicineRequest.Urgency urgency;
        private String location;
        private String details;
        private MedicineRequest.RequestStatus requestStatus;
        private LocalDateTime createdAt;
        private Long userId;
        private String userName;
        private String userPhone;

        public static Response from(MedicineRequest req) {
            Response r = new Response();
            r.setId(req.getId());
            r.setMedicineName(req.getMedicineName());
            r.setQuantity(req.getQuantity());
            r.setUrgency(req.getUrgency());
            r.setLocation(req.getLocation());
            r.setDetails(req.getDetails());
            r.setRequestStatus(req.getRequestStatus());
            r.setCreatedAt(req.getCreatedAt());
            if (req.getUser() != null) {
                r.setUserId(req.getUser().getId());
                r.setUserName(req.getUser().getName());
                r.setUserPhone(req.getUser().getPhone());
            }
            return r;
        }
    }
}
