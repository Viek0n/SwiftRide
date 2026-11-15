package com.mircroservice.trip_service.domain.states;

import com.mircroservice.trip_service.domain.enums.TripStatus;

public class InprogressState implements TripState {
    @Override
    public TripStatus getStatus() {
        return TripStatus.INPROGRESS;
    }

    @Override
    public TripState complete() {
        return new CompletedState();
    }
}
