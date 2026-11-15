package com.mircroservice.trip_service.infrastructure.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mircroservice.trip_service.application.dto.TripCreateRequest;
import com.mircroservice.trip_service.domain.entities.Trip;
import com.mircroservice.trip_service.domain.repositories.TripRepository;

@Service
public class TripService {
    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public Trip createTrip(TripCreateRequest request) {
        var trip = new Trip(
            UUID.randomUUID().toString(),
            request.from(),
            request.to(),
            request.riderId(),
            Instant.now()
        );
        return tripRepository.save(trip);
    }

    public Trip getTrip(String tripId) {
        return tripRepository.findById(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId));
    }

    public List<Trip> getTripsForRider(String riderId) {
        if (riderId == null || riderId.isBlank()) {
            throw new IllegalArgumentException("riderId must not be blank");
        }
        return tripRepository.findByRiderId(riderId);
    }

    public Trip assignDriver(String tripId, String driverId) {
        var trip = getTrip(tripId);
        trip.assignDriver(driverId);
        return tripRepository.save(trip);
    }

    public Trip markPickedUp(String tripId) {
        var trip = getTrip(tripId);
        trip.markPickedUp();
        return tripRepository.save(trip);
    }

    public Trip completeTrip(String tripId) {
        var trip = getTrip(tripId);
        trip.complete();
        return tripRepository.save(trip);
    }

    public Trip cancelTrip(String tripId) {
        var trip = getTrip(tripId);
        trip.cancel();
        return tripRepository.save(trip);
    }

    public Trip markMatchingFailed(String tripId) {
        var trip = getTrip(tripId);
        trip.markMatchingFailed();
        return tripRepository.save(trip);
    }
}
