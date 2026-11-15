package com.mircroservice.matching_service.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.entities.MatchingAttempt;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.MatchingAttemptRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

class MatchingRideUseCaseTest {
    @Test
    void asksOnlyUnaskedDriversAcrossTripServiceMatchingCallsAndStopsAtThree() {
        GetNearbyActiveDriversUseCase nearbyDrivers = mock(GetNearbyActiveDriversUseCase.class);
        ActiveDriverRepository driverRepository = mock(ActiveDriverRepository.class);
        MatchingAttemptRepository attemptRepository = mock(MatchingAttemptRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        List<MatchingAttempt> storedAttempts = new ArrayList<>();
        Position origin = new Position(10.0, 106.0);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user("user-1")));
        when(attemptRepository.findByTripId(anyString()))
            .thenAnswer(invocation -> List.copyOf(storedAttempts));
        when(attemptRepository.save(any(MatchingAttempt.class))).thenAnswer(invocation -> {
            MatchingAttempt attempt = invocation.getArgument(0);
            storedAttempts.add(attempt);
            return attempt;
        });
        when(attemptRepository.findByTripIdAndDriverId(anyString(), anyString())).thenAnswer(invocation ->
            storedAttempts.stream()
                .filter(attempt -> attempt.getTripId().equals(invocation.getArgument(0)))
                .filter(attempt -> attempt.getDriverId().equals(invocation.getArgument(1)))
                .findFirst()
        );
        when(nearbyDrivers.execute(origin)).thenReturn(List.of(
            driver("driver-1", origin),
            driver("driver-2", origin),
            driver("driver-3", origin)
        ));
        when(driverRepository.transitionStatus(anyString(), any(ActiveStatus.class), any(ActiveStatus.class)))
            .thenReturn(true);

        MatchingRideUseCase useCase = new MatchingRideUseCase(
            nearbyDrivers,
            driverRepository,
            attemptRepository,
            userRepository
        );

        MatchingAttempt attempt = useCase.start("trip-1", "user-1", origin).orElseThrow();
        assertEquals("driver-1", attempt.getDriverId());
        useCase.decline("trip-1", attempt.getDriverId());

        attempt = useCase.start("trip-1", "user-1", origin).orElseThrow();
        assertEquals("driver-2", attempt.getDriverId());
        useCase.decline("trip-1", attempt.getDriverId());

        attempt = useCase.start("trip-1", "user-1", origin).orElseThrow();
        assertEquals("driver-3", attempt.getDriverId());
        useCase.decline("trip-1", attempt.getDriverId());

        org.junit.jupiter.api.Assertions.assertTrue(useCase.start("trip-1", "user-1", origin).isEmpty());
        assertEquals(3, storedAttempts.size());
        assertEquals(List.of("driver-1", "driver-2", "driver-3"),
            storedAttempts.stream().map(MatchingAttempt::getDriverId).toList());
        assertEquals(List.of("trip-1", "trip-1", "trip-1"),
            storedAttempts.stream().map(MatchingAttempt::getTripId).toList());
        assertEquals(List.of("user-1", "user-1", "user-1"),
            storedAttempts.stream().map(MatchingAttempt::getUserId).toList());
    }

    @Test
    void acceptedDriverCanBeConfirmedWithoutPersistingMatchingSession() {
        GetNearbyActiveDriversUseCase nearbyDrivers = mock(GetNearbyActiveDriversUseCase.class);
        ActiveDriverRepository driverRepository = mock(ActiveDriverRepository.class);
        MatchingAttemptRepository attemptRepository = mock(MatchingAttemptRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        Position origin = new Position(10.0, 106.0);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user("user-1")));
        when(attemptRepository.findByTripId("trip-1")).thenReturn(List.of());
        MatchingAttempt attempt = new MatchingAttempt("trip-1", "user-1", "driver-1");
        when(attemptRepository.save(any(MatchingAttempt.class))).thenReturn(attempt);
        when(attemptRepository.findByTripIdAndDriverId("trip-1", "driver-1"))
            .thenReturn(Optional.of(attempt));
        when(nearbyDrivers.execute(origin)).thenReturn(List.of(driver("driver-1", origin)));
        when(driverRepository.transitionStatus(anyString(), any(ActiveStatus.class), any(ActiveStatus.class)))
            .thenReturn(true);

        MatchingRideUseCase useCase = new MatchingRideUseCase(
            nearbyDrivers,
            driverRepository,
            attemptRepository,
            userRepository
        );

        useCase.start("trip-1", "user-1", origin).orElseThrow();
        useCase.accept("trip-1", "driver-1");
        useCase.confirm("trip-1", "driver-1");

        org.mockito.Mockito.verify(driverRepository).transitionStatus(
            "driver-1",
            ActiveStatus.MATCHING,
            ActiveStatus.MATCH_FOUND
        );
        org.mockito.Mockito.verify(driverRepository).transitionStatus(
            "driver-1",
            ActiveStatus.MATCH_FOUND,
            ActiveStatus.IN_MATCH
        );
    }

    private ActiveDriver driver(String userId, Position position) {
        return new ActiveDriver(userId, ActiveStatus.ONLINE, position);
    }

    private User user(String userId) {
        return new User(userId, "Rider", "0900000000", "hash", AccountStatus.ACTIVE, null, null);
    }
}
