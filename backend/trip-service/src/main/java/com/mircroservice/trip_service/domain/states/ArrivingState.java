package com.mircroservice.trip_service.domain.states;

import com.mircroservice.trip_service.domain.enums.TripStatus;

public class ArrivingState implements TripState {
    @Override
    public TripStatus getStatus() {
        return TripStatus.PICKUP;
    }

    @Override
    public TripState markPickedUp() {
        return new InprogressState();
    }

    @Override
    public TripState cancel() {
        return new CancelledState();
    }
}
