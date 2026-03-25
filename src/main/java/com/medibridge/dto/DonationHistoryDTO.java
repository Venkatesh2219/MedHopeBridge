package com.medibridge.dto;

import com.medibridge.model.DonationHistory;
import lombok.Data;
import java.time.LocalDateTime;

public class DonationHistoryDTO {

    @Data
    public static class ContactRequest {
        private Long medicineId;
        private String message;
    }

    @Data
    public static class FulfillRequest {
        private Long requestId;
        private String message;
    }

    @Data
    public static class Response {
        private Long id;
        private String medicineName;
        private String quantity;
        private String message;
        private DonationHistory.DonationStatus status;
        private LocalDateTime createdAt;
        private Long donorId;
        private String donorName;
        private String donorPhone;
        private Long receiverId;
        private String receiverName;
        private String receiverPhone;

        public static Response from(DonationHistory d) {
            Response r = new Response();
            r.setId(d.getId());
            r.setMedicineName(d.getMedicineName());
            r.setQuantity(d.getQuantity());
            r.setMessage(d.getMessage());
            r.setStatus(d.getStatus());
            r.setCreatedAt(d.getCreatedAt());
            if (d.getDonor() != null) {
                r.setDonorId(d.getDonor().getId());
                r.setDonorName(d.getDonor().getName());
                r.setDonorPhone(d.getDonor().getPhone());
            }
            if (d.getReceiver() != null) {
                r.setReceiverId(d.getReceiver().getId());
                r.setReceiverName(d.getReceiver().getName());
                r.setReceiverPhone(d.getReceiver().getPhone());
            }
            return r;
        }
    }
}
