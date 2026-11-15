package com.mircroservice.trip_service.infrastructure.services;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String tripId) {
        super("Trip not found: " + tripId);
    }
}
