package com.mircroservice.matching_service.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

class MatchingRideUseCaseTest {
    @Test
    void marksNearestDriverAsMatchedAndPersistsTheStatus() {
        GetNearbyActiveDriversUseCase getNearbyActiveDrivers = mock(GetNearbyActiveDriversUseCase.class);
        ActiveDriverRepository activeDriverRepository = mock(ActiveDriverRepository.class);
        Position riderPosition = new Position(10.0, 106.0);
        ActiveDriver driver = new ActiveDriver("driver-1", ActiveStatus.ONLINE, riderPosition);
        when(getNearbyActiveDrivers.execute(riderPosition)).thenReturn(List.of(driver));
        when(activeDriverRepository.save(driver)).thenReturn(driver);

        ActiveDriver matched = new MatchingRideUseCase(
            getNearbyActiveDrivers,
            activeDriverRepository
        ).execute(riderPosition);

        assertEquals(ActiveStatus.MATCH_FOUND, matched.getActiveStatus());
        verify(activeDriverRepository).save(driver);
    }
}
