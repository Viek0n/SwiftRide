package com.mircroservice.matching_service.application.usecases;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.entities.MatchingAttempt;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.MatchingAttemptRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

@Service
public class MatchingRideUseCase {
    private static final int MAX_DRIVER_ATTEMPTS = 3;

    private final GetNearbyActiveDriversUseCase getNearbyActiveDrivers;
    private final ActiveDriverRepository activeDriverRepository;
    private final MatchingAttemptRepository matchingAttemptRepository;
    private final UserRepository userRepository;

    public MatchingRideUseCase(
        GetNearbyActiveDriversUseCase getNearbyActiveDrivers,
        ActiveDriverRepository activeDriverRepository,
        MatchingAttemptRepository matchingAttemptRepository,
        UserRepository userRepository
    ) {
        this.getNearbyActiveDrivers = getNearbyActiveDrivers;
        this.activeDriverRepository = activeDriverRepository;
        this.matchingAttemptRepository = matchingAttemptRepository;
        this.userRepository = userRepository;
    }

    public Optional<MatchingAttempt> start(String tripId, String userId, Position riderPosition) {
        if (tripId == null || tripId.isBlank()) {
            throw new IllegalArgumentException("Trip ID is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (riderPosition == null) {
            throw new IllegalArgumentException("Rider position is required");
        }

        User user = userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("User was not found")
        );
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("User account is not active");
        }

        List<MatchingAttempt> attempts = matchingAttemptRepository.findByTripId(tripId);
        if (attempts.size() >= MAX_DRIVER_ATTEMPTS) {
            return Optional.empty();
        }

        for (var driver : getNearbyActiveDrivers.execute(riderPosition)) {
            boolean alreadyAsked = attempts.stream()
                .anyMatch(attempt -> attempt.getDriverId().equals(driver.getUserId()));
            if (alreadyAsked) {
                continue;
            }
            if (!activeDriverRepository.transitionStatus(
                driver.getUserId(),
                ActiveStatus.ONLINE,
                ActiveStatus.MATCHING
            )) {
                continue;
            }

            return Optional.of(matchingAttemptRepository.save(new MatchingAttempt(
                tripId,
                userId,
                driver.getUserId()
            )));
        }

        return Optional.empty();
    }

    public MatchingAttempt accept(String tripId, String driverId) {
        MatchingAttempt attempt = requireAttempt(tripId, driverId);
        transitionDriver(driverId, ActiveStatus.MATCHING, ActiveStatus.MATCH_FOUND);
        return attempt;
    }

    public MatchingAttempt decline(String tripId, String driverId) {
        MatchingAttempt attempt = requireAttempt(tripId, driverId);
        transitionDriver(driverId, ActiveStatus.MATCHING, ActiveStatus.ONLINE);
        return attempt;
    }

    public MatchingAttempt confirm(String tripId, String driverId) {
        MatchingAttempt attempt = requireAttempt(tripId, driverId);
        transitionDriver(driverId, ActiveStatus.MATCH_FOUND, ActiveStatus.IN_MATCH);
        return attempt;
    }

    public int getAttemptedDriverCount(String tripId) {
        return matchingAttemptRepository.findByTripId(tripId).size();
    }

    private MatchingAttempt requireAttempt(String tripId, String driverId) {
        if (tripId == null || tripId.isBlank() || driverId == null || driverId.isBlank()) {
            throw new IllegalArgumentException("Trip ID and driver ID are required");
        }
        return matchingAttemptRepository.findByTripIdAndDriverId(tripId, driverId).orElseThrow(
            () -> new IllegalArgumentException("Driver was not asked to match this trip")
        );
    }

    private void transitionDriver(String driverId, ActiveStatus current, ActiveStatus next) {
        if (!activeDriverRepository.transitionStatus(driverId, current, next)) {
            throw new IllegalStateException("Driver status does not allow this match decision");
        }
    }
}
