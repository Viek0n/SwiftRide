package com.mircroservice.matching_service.application.usecases.matching;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mircroservice.matching_service.application.service.MatchingWorkflowService;
import com.mircroservice.matching_service.domain.entities.MatchingSession;
import com.mircroservice.matching_service.domain.enumerators.MatchingStatus;
import com.mircroservice.matching_service.domain.repositories.MatchingSessionRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

@ExtendWith(MockitoExtension.class)
class MatchingRiderWithDriverTest {
    @Mock
    private MatchingSessionRepository sessionRepository;

    @Mock
    private MatchingWorkflowService workflowService;

    @InjectMocks
    private MatchingRiderWithDriver matchingRiderWithDriver;

    @Test
    void startsWorkflowWithPersistedSessionId() {
        Position riderPosition = new Position(1.0, 2.0);
        when(sessionRepository.findByTripId("trip-1")).thenReturn(Optional.empty());
        when(sessionRepository.save(any(MatchingSession.class))).thenAnswer(invocation -> {
            MatchingSession savedSession = invocation.getArgument(0);
            savedSession.setId("session-1");
            return savedSession;
        });

        matchingRiderWithDriver.execute(riderPosition, "trip-1");

        verify(workflowService).tryNextDriver("session-1");
    }

    @Test
    void resumesSearchingSessionInsteadOfCreatingAnother() {
        MatchingSession session = new MatchingSession("trip-1", new Position(1.0, 2.0), 3);
        session.setId("session-1");
        when(sessionRepository.findByTripId("trip-1")).thenReturn(Optional.of(session));

        matchingRiderWithDriver.execute(new Position(1.0, 2.0), "trip-1");

        verify(sessionRepository, never()).save(any(MatchingSession.class));
        verify(workflowService).tryNextDriver("session-1");
    }

    @Test
    void ignoresDuplicateConfirmationForSessionAlreadyInProgress() {
        MatchingSession session = new MatchingSession("trip-1", new Position(1.0, 2.0), 3);
        session.setId("session-1");
        session.setStatus(MatchingStatus.WAITING_FOR_DRIVER);
        when(sessionRepository.findByTripId("trip-1")).thenReturn(Optional.of(session));

        matchingRiderWithDriver.execute(new Position(1.0, 2.0), "trip-1");

        verify(sessionRepository, never()).save(any(MatchingSession.class));
        verify(workflowService, never()).tryNextDriver(any());
    }
}
