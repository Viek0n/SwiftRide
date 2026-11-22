package com.mircroservice.trip_service.domain.states;

import com.mircroservice.trip_service.domain.enums.TripStatus;

public class PickUpState implements TripState{

    @Override
    public TripStatus getStatus() {
        return TripStatus.PICKUP;
    }

    @Override
    public TripState pickedUp(){
        return new InprogressState();
    }
}
