package com.mircroservice.matching_service.application.usecases.matching;

import com.mircroservice.matching_service.application.exceptions.OfferExpiredException;
import com.mircroservice.matching_service.application.service.MatchingWorkflowService;
import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.entities.MatchingSession;
import com.mircroservice.matching_service.domain.entities.TripOffer;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.MatchingSessionRepository;
import com.mircroservice.matching_service.domain.repositories.TripOfferRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.util.Map;
import java.util.UUID;
import java.time.Instant;

@Service
public class TripOfferAcceptedUseCase {
    private final TripOfferRepository offerRepository;
    private final MatchingSessionRepository sessionRepository;
    private final ActiveDriverRepository activeDriverRepository;
    private final MatchingSessionRepository matchingSessionRepository;
    private final RabbitTemplate rabbitTemplate;

    public TripOfferAcceptedUseCase(
            TripOfferRepository offerRepository,
            MatchingSessionRepository sessionRepository,
            ActiveDriverRepository activeDriverRepository,
            MatchingSessionRepository matchingSessionRepository,
            RabbitTemplate rabbitTemplate) {
        this.offerRepository = offerRepository;
        this.sessionRepository = sessionRepository;
        this.activeDriverRepository = activeDriverRepository;
        this.matchingSessionRepository = matchingSessionRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public TripOfferAcceptedUseCase(
            TripOfferRepository offerRepository,
            MatchingSessionRepository sessionRepository,
            ActiveDriverRepository activeDriverRepository,
            MatchingSessionRepository matchingSessionRepository) {
        this(offerRepository, sessionRepository, activeDriverRepository, matchingSessionRepository, null);
    }

    // Publish DriverMatched.
    public void execute(String offerId, String driverId){
        TripOffer offer = offerRepository.findById(offerId)
            .orElseThrow(() ->
                new IllegalArgumentException("Offer not found"));

        MatchingSession session =
            sessionRepository.findByCurrentOfferId(offerId)
                .orElseThrow(() ->
                    new IllegalArgumentException("Session not found"));

        ActiveDriver driver =
            activeDriverRepository.findById(driverId)
                .orElseThrow(() ->
                    new IllegalArgumentException("Driver not found"));

        // Validate ownership
        if (!offer.getDriverId().equals(driverId)) {
            throw new IllegalArgumentException(
                "Offer does not belong to this driver");
        }

        // Validate offer
        if (offer.isExpired()) {
            throw new OfferExpiredException(offerId);
        }

        if (!offer.isPending()) {
            throw new IllegalStateException(
                "Offer is not pending");
        }

        // Accept offer.
        offer.accept();
        offerRepository.save(offer);

        // Driver go
        driver.acceptMatch();
        activeDriverRepository.save(driver);

        session.markMatched();
        matchingSessionRepository.save(session);

        if (rabbitTemplate != null) rabbitTemplate.convertAndSend(
                "matching.exchange",
                "driver.accepted",
                Map.of(
                        "eventId", UUID.randomUUID().toString(),
                        "tripId", offer.getTripId(),
                        "riderId", "",
                        "driverId", driverId,
                        "offerId", offer.getId(),
                        "assignedAt", Instant.now().toString()));
    }
}
