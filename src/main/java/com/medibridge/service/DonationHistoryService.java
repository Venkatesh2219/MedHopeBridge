package com.medibridge.service;

import com.medibridge.dto.DonationHistoryDTO;
import java.util.List;

public interface DonationHistoryService {
    DonationHistoryDTO.Response contactDonor(Long receiverId, DonationHistoryDTO.ContactRequest req);
    DonationHistoryDTO.Response fulfillRequest(Long donorId, DonationHistoryDTO.FulfillRequest req);
    List<DonationHistoryDTO.Response> getHistoryForUser(Long userId);
    List<DonationHistoryDTO.Response> getDonationsMadeByUser(Long userId);
    List<DonationHistoryDTO.Response> getRequestsFulfilledForUser(Long userId);
}
