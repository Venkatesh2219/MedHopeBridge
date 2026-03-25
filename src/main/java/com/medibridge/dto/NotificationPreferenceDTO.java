package com.medibridge.dto;

import com.medibridge.model.NotificationPreference;
import lombok.Data;

public class NotificationPreferenceDTO {

    @Data
    public static class Request {
        private Boolean expiryReminders;
        private Boolean nearbyDonations;
        private Boolean requestUpdates;
        private Boolean communityUpdates;
        private Integer reminderLeadDays;
    }

    @Data
    public static class Response {
        private Boolean expiryReminders;
        private Boolean nearbyDonations;
        private Boolean requestUpdates;
        private Boolean communityUpdates;
        private Integer reminderLeadDays;

        public static Response from(NotificationPreference p) {
            Response r = new Response();
            r.setExpiryReminders(p.getExpiryReminders());
            r.setNearbyDonations(p.getNearbyDonations());
            r.setRequestUpdates(p.getRequestUpdates());
            r.setCommunityUpdates(p.getCommunityUpdates());
            r.setReminderLeadDays(p.getReminderLeadDays());
            return r;
        }
    }
}
