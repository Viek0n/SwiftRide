package com.mircroservice.matching_service.application.usecases.matching;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.application.service.MatchingWorkflowService;
import com.mircroservice.matching_service.domain.constants.MatchingConstant;
import com.mircroservice.matching_service.domain.entities.MatchingSession;
import com.mircroservice.matching_service.domain.enumerators.MatchingStatus;
import com.mircroservice.matching_service.domain.repositories.MatchingSessionRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

@Service
public class MatchingRiderWithDriver {

    private final MatchingSessionRepository sessionRepository;
    private final MatchingWorkflowService workflowService;

    public MatchingRiderWithDriver(
        MatchingSessionRepository sessionRepository,
        MatchingWorkflowService workflowService
    ) {
        this.sessionRepository = sessionRepository;
        this.workflowService = workflowService;
    }

    public void execute(Position riderPosition, String tripId) {
        var existingSession = sessionRepository.findByTripId(tripId);
        if (existingSession.isPresent()) {
            MatchingSession session = existingSession.get();
            if (session.getStatus() == MatchingStatus.SEARCHING) {
                workflowService.tryNextDriver(session.getId());
            }
            return;
        }

        MatchingSession session = new MatchingSession(
            tripId,
            riderPosition,
            MatchingConstant.MAX_MATCHING_ATTEMPT
        );

        MatchingSession savedSession = sessionRepository.save(session);

        workflowService.tryNextDriver(savedSession.getId());
    }
}
