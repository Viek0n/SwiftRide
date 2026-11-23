package com.mircroservice.matching_service.application.usecases.matching;

import com.mircroservice.matching_service.application.service.MatchingWorkflowService;
import com.mircroservice.matching_service.application.exceptions.OfferExpiredException;
import com.mircroservice.matching_service.application.exceptions.OfferException;
import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.entities.MatchingSession;
import com.mircroservice.matching_service.domain.entities.TripOffer;
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
        TripOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));
        MatchingSession session = sessionRepository.findByCurrentOfferId(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!offer.getDriverId().equals(driverId)) {
            throw new IllegalArgumentException("Offer does not belong to this driver");
        }
        if (!offer.isPending()) {
            throw new IllegalStateException("Offer is not pending");
        }
        if (!offer.isExpired()) {
            throw new OfferException("Offer is not expired");
        }

        offer.expire();
        offerRepository.save(offer);
        ActiveDriver driver = activeDriverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        driver.rejectMatch();
        activeDriverRepository.save(driver);
        session.excludeCurrentDriver();
        sessionRepository.save(session);
        workflowService.tryNextDriver(session.getId());
    }  
}
