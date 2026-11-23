package com.mircroservice.trip_service.domain.states;

import com.mircroservice.trip_service.domain.enums.TripStatus;

public class RequestedState implements TripState {
    @Override
    public TripStatus getStatus() {
        return TripStatus.REQUESTED;
    }

    @Override
    public TripState startMatching() {
        return new MatchingState();
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
