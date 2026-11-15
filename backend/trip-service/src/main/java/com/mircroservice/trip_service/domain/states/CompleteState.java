package com.mircroservice.trip_service.domain.states;

import com.mircroservice.trip_service.domain.enums.TripStatus;

public class CompleteState implements TripState {
    @Override
    public TripStatus getStatus() {
        return TripStatus.COMPLETE;
    }
}
