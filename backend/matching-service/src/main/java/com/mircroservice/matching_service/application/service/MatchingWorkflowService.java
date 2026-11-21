package com.mircroservice.matching_service.application.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.mircroservice.matching_service.domain.constants.MatchingConstant;
import com.mircroservice.matching_service.domain.dto.requests.TripOfferRequest;
import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.entities.MatchingSession;
import com.mircroservice.matching_service.domain.entities.TripOffer;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.MatchingSessionRepository;
import com.mircroservice.matching_service.domain.repositories.TripOfferRepository;

public class MatchingWorkflowService {
    private final ActiveDriverRepository activeDriverRepository;
    private final MatchingSessionRepository sessionRepository;
    private final TripOfferRepository offerRepository;
    private final RabbitTemplate rabbitTemplate;

    public MatchingWorkflowService(
        ActiveDriverRepository activeDriverRepository,
        MatchingSessionRepository sessionRepository,
        TripOfferRepository offerRepository,
        RabbitTemplate rabbitTemplate
    ){
        this.activeDriverRepository = activeDriverRepository;
        this.sessionRepository = sessionRepository;
        this.offerRepository = offerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void tryNextDriver(String sessionId) {

        MatchingSession session = sessionRepository
            .findById(sessionId)
            .orElseThrow();

        if (session.getAttempt() >= MatchingConstant.MAX_MATCHING_ATTEMPT) {
            failMatching(session);
            return;
        }

        Optional<ActiveDriver> result =
            activeDriverRepository.findNearestAvailableDriver(
                session.getRiderPosition(),
                new ArrayList<>(session.getExcludedDriverIds())
            );

        if (result.isEmpty()) {
            failMatching(session);
            return;
        }

        ActiveDriver driver = result.get();

        boolean reserved = activeDriverRepository.tryReserveDriver(
            driver.getUserId(),
            session.getTripId()
        );

        if (!reserved) {
            session.excludeCurrentDriver();
            sessionRepository.save(session);

            tryNextDriver(sessionId);
            return;
        }

        TripOffer offer = new TripOffer(
            session.getTripId(),
            driver.getUserId()
        );

        offerRepository.save(offer);

        session.startWaiting(
            driver.getUserId(),
            offer.getId()
        );

        sessionRepository.save(session);

        rabbitTemplate.convertAndSend(
            "matching.exchange",
            "driver.offer.requested",
            new TripOfferRequest(
                offer.getId(),
                session.getTripId(),
                driver.getUserId(),
                session.getAttempt()
            )
        );
    }

    private void failMatching(MatchingSession session) {
        session.fail();
        sessionRepository.save(session);
    }
}
