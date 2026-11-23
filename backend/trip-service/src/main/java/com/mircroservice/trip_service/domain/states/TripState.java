package com.mircroservice.trip_service.domain.states;

import com.mircroservice.trip_service.domain.enums.TripStatus;

public interface TripState {
    TripStatus getStatus();

    default TripState assignDriver(String driverId) {
        throw invalidTransition("assign a driver");
    }

    default TripState paid(){
        throw invalidTransition("paid the trip");
    }

    default TripState pickUp(){
        throw invalidTransition("trip pickup");
    }

    default TripState pickedUp(){
        throw invalidTransition("rider pickup");
    }
    default TripState complete() {
        throw invalidTransition("complete the trip");
    }

    default TripState cancel() {
        throw invalidTransition("cancel the trip");
    }

    default TripState markMatchingFailed() {
        throw invalidTransition("mark matching as failed");
    }

    private IllegalStateException invalidTransition(String action) {
        return new IllegalStateException(
            "Cannot " + action + " while trip status is " + getStatus()
        );
    }

    static TripState from(TripStatus status) {
        if (status == null) {
            throw new IllegalStateException("Trip status must not be null");
        }

        return switch (status) {
            case REQUESTED -> new RequestedState();
            case MATCHING -> new MatchingState();
            case MATCHINGFAIL -> new MatchingFailedState();
            case PICKUP -> new PickUpState();
            case INPROGRESS -> new InprogressState();
            case CANCELLED -> new CancelledState();
            case COMPLETE -> new CompleteState();
            case PAID -> new PaidState();
        };
    }

    default TripState startMatching() {
        throw invalidTransition("start matching");
    }
}
