package com.mircroservice.trip_service.infrastructure.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mircroservice.trip_service.application.dto.TripCreateRequest;
import com.mircroservice.trip_service.domain.entities.Trip;
import com.mircroservice.trip_service.domain.repositories.TripRepository;
import com.mircroservice.trip_service.domain.enums.TripStatus;
import com.mircroservice.trip_service.domain.value_objects.TripRoute;
import com.mircroservice.trip_service.infrastructure.clients.MatchingServiceClient;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final MatchingServiceClient matchingService;

    public TripService(TripRepository tripRepository, MatchingServiceClient matchingService) {
        this.tripRepository = tripRepository;
        this.matchingService = matchingService;
    }

    public Trip createTrip(TripCreateRequest request) {
        var routeResponse = matchingService.getRoute(request.from(), request.to());
        var fareResponse = matchingService.calculateFare(request.from(), request.to());
        var route = new TripRoute(
            routeResponse.distanceMeters(),
            routeResponse.durationSeconds(),
            new TripRoute.Geometry(
                routeResponse.geometry().type(),
                routeResponse.geometry().coordinates()
            )
        );
        var trip = new Trip(
            UUID.randomUUID().toString(),
            request.from(),
            request.to(),
            request.riderId(),
            route,
            fareResponse.totalFare(),
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
        trip.requireMatchingDriver(driverId);
        matchingService.accept(tripId, driverId);
        matchingService.confirm(tripId, driverId);
        trip.assignDriver(driverId);
        return tripRepository.save(trip);
    }

    public Trip confirmTrip(String tripId) {
        var trip = getTrip(tripId);
        if (trip.getStatus() != TripStatus.REQUESTED) {
            throw new IllegalStateException("Only a requested trip can start matching");
        }

        Optional<MatchingServiceClient.MatchResult> match = matchingService.match(
            trip.getId(),
            trip.getRiderId(),
            trip.getFrom()
        );
        if (match.isEmpty()) {
            trip.markMatchingFailed();
        } else {
            trip.startMatching(match.get().driverId());
        }
        return tripRepository.save(trip);
    }

    public Trip rejectDriver(String tripId, String driverId) {
        var trip = getTrip(tripId);
        trip.requireMatchingDriver(driverId);
        matchingService.decline(tripId, driverId);

        Optional<MatchingServiceClient.MatchResult> nextMatch = matchingService.match(
            trip.getId(),
            trip.getRiderId(),
            trip.getFrom()
        );
        if (nextMatch.isEmpty()) {
            trip.markMatchingFailed();
        } else {
            trip.updateMatchingDriver(nextMatch.get().driverId());
        }
        return tripRepository.save(trip);
    }

    public Trip completeTrip(String tripId, String driverId) {
        var trip = getTrip(tripId);
        trip.complete(driverId);
        return tripRepository.save(trip);
    }

    public Trip cancelTrip(String tripId) {
        var trip = getTrip(tripId);
        if (trip.getStatus() == TripStatus.MATCHING && trip.getDriverId() != null) {
            matchingService.decline(tripId, trip.getDriverId());
        }
        trip.cancel();
        return tripRepository.save(trip);
    }

    public Trip markMatchingFailed(String tripId) {
        var trip = getTrip(tripId);
        trip.markMatchingFailed();
        return tripRepository.save(trip);
    }
}
