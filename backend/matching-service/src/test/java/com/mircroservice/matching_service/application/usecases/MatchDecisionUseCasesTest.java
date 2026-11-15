package com.mircroservice.matching_service.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

class MatchDecisionUseCasesTest {
    @Test
    void acceptingMatchMovesDriverIntoTrip() {
        ActiveDriverRepository repository = mock(ActiveDriverRepository.class);
        ActiveDriver driver = activeDriver(ActiveStatus.MATCH_FOUND);
        when(repository.findById("driver-1")).thenReturn(Optional.of(driver));
        when(repository.save(driver)).thenReturn(driver);

        ActiveDriver result = new DriverMatchAcceptedUseCase(repository).execute("driver-1");

        assertEquals(ActiveStatus.IN_MATCH, result.getActiveStatus());
    }

    @Test
    void rejectingMatchReturnsDriverToOnlinePool() {
        ActiveDriverRepository repository = mock(ActiveDriverRepository.class);
        ActiveDriver driver = activeDriver(ActiveStatus.MATCH_FOUND);
        when(repository.findById("driver-1")).thenReturn(Optional.of(driver));
        when(repository.save(driver)).thenReturn(driver);

        ActiveDriver result = new MatchRejectedUseCase(repository).execute("driver-1");

        assertEquals(ActiveStatus.ONLINE, result.getActiveStatus());
    }

    private ActiveDriver activeDriver(ActiveStatus status) {
        return new ActiveDriver("driver-1", status, new Position(10.0, 106.0));
    }
}
