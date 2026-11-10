package com.mircroservice.trip_service.domain.states;

import com.mircroservice.trip_service.domain.enums.TripStatus;

public class MatchingState implements TripState {
    @Override
    public TripStatus getStatus() {
        return TripStatus.MATCHING;
    }

    @Override
    public TripState assignDriver(String driverId) {
        if (driverId == null || driverId.isBlank()) {
            throw new IllegalArgumentException("driverId must not be blank");
        }
        return new ArrivingState();
    }

    @Override
    public TripState cancel() {
        return new CancelledState();
    }

    @Override
    public TripState markMatchingFailed() {
        return new MatchingFailedState();
    }
}
