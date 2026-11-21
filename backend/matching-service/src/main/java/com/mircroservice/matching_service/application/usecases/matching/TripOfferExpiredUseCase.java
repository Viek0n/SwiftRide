package com.mircroservice.matching_service.application.usecases.matching;

import com.mircroservice.matching_service.application.service.MatchingWorkflowService;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.MatchingSessionRepository;
import com.mircroservice.matching_service.domain.repositories.TripOfferRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor 
public class TripOfferExpiredUseCase {

    private final TripOfferRepository offerRepository;
    private final MatchingSessionRepository sessionRepository;
    private final ActiveDriverRepository activeDriverRepository;
    private final MatchingWorkflowService workflowService;

    public void execute(String offerId, String driverId){
        // Find session.
        // Confirm that it is still waiting.
        // Confirm that the offer is still pending.
        // Mark offer as expired.
        // Release driver.
        // Exclude driver.
        // Start the next matching attempt.
    }  
}
