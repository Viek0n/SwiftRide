package com.mircroservice.matching_service.domain.dto.responses;

import com.mircroservice.matching_service.domain.entities.MatchingAttempt;

public record MatchingResponse(
    String tripId,
    String userId,
    String driverId,
    int attemptedDrivers
) {
    public static MatchingResponse toResponse(MatchingAttempt attempt, int attemptedDrivers) {
        return new MatchingResponse(
            attempt.getTripId(),
            attempt.getUserId(),
            attempt.getDriverId(),
            attemptedDrivers
        );
    }
}
