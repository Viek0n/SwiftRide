package com.mircroservice.matching_service.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

class DriverMatchingUseCaseTest {
    @Test
    void locationUpdateCannotMakeAReservedDriverAvailableAgain() {
        DriverProfileRepository profileRepository = mock(DriverProfileRepository.class);
        ActiveDriverRepository activeDriverRepository = mock(ActiveDriverRepository.class);
        when(profileRepository.findById("driver-1")).thenReturn(Optional.of(
            new DriverProfile("driver-1", "license", "plate", AccountStatus.ACTIVE, null, null)
        ));
        when(activeDriverRepository.findById("driver-1")).thenReturn(Optional.of(
            new ActiveDriver("driver-1", ActiveStatus.MATCHING, new Position(10.0, 106.0))
        ));

        DriverMatchingUseCase useCase = new DriverMatchingUseCase(
            profileRepository,
            activeDriverRepository
        );

        assertThrows(
            IllegalStateException.class,
            () -> useCase.execute("driver-1", new Position(10.1, 106.1))
        );
        verify(activeDriverRepository, never()).updatePositionIfStatus(
            "driver-1",
            ActiveStatus.MATCHING,
            new Position(10.1, 106.1)
        );
    }

    @Test
    void locationUpdateAtomicallyPublishesDriverAsOnline() {
        DriverProfileRepository profileRepository = mock(DriverProfileRepository.class);
        ActiveDriverRepository activeDriverRepository = mock(ActiveDriverRepository.class);
        Position position = new Position(10.0, 106.0);
        when(profileRepository.findById("driver-1")).thenReturn(Optional.of(
            new DriverProfile("driver-1", "license", "plate", AccountStatus.ACTIVE, null, null)
        ));
        AtomicInteger lookupCount = new AtomicInteger();
        when(activeDriverRepository.findById("driver-1")).thenAnswer(invocation -> Optional.of(
            new ActiveDriver(
                "driver-1",
                lookupCount.getAndIncrement() == 0 ? ActiveStatus.OFFLINE : ActiveStatus.ONLINE,
                position
            )
        ));
        when(activeDriverRepository.updatePositionIfStatus(
            "driver-1",
            ActiveStatus.OFFLINE,
            position
        )).thenReturn(true);

        ActiveDriver result = new DriverMatchingUseCase(
            profileRepository,
            activeDriverRepository
        ).execute("driver-1", position);

        assertEquals(ActiveStatus.ONLINE, result.getActiveStatus());
        verify(activeDriverRepository).updatePositionIfStatus(
            "driver-1",
            ActiveStatus.OFFLINE,
            position
        );
    }
}
