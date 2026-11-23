package com.mircroservice.trip_service.infrastructure.services;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mircroservice.trip_service.api.security.AuthenticatedUser;
import com.mircroservice.trip_service.application.dto.TripCreateRequest;
import com.mircroservice.trip_service.domain.entities.OutboxEvent;
import com.mircroservice.trip_service.domain.entities.Trip;
import com.mircroservice.trip_service.domain.repositories.OutboxRepository;
import com.mircroservice.trip_service.domain.repositories.TripRepository;
import com.mircroservice.trip_service.domain.enums.TripStatus;
import com.mircroservice.trip_service.domain.value_objects.TripRoute;
import com.mircroservice.trip_service.infrastructure.clients.MatchingServiceClient;
import com.mircroservice.trip_service.infrastructure.documents.OutboxDocument;
import com.mircroservice.trip_service.infrastructure.messaging.events.TripMatchingRequestedEvent;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final MatchingServiceClient matchingService;
    private final OutboxRepository outboxRepository;

    public TripService(TripRepository tripRepository, MatchingServiceClient matchingService, OutboxRepository outboxRepository) {
        this.tripRepository = tripRepository;
        this.matchingService = matchingService;
        this.outboxRepository = outboxRepository;
    }

    public Trip createTrip(TripCreateRequest request) {
        var routeResponse = matchingService.getRoute(request.from(), request.to());
        var fareResponse = matchingService.calculateFare(request.from(), request.to());
        String riderId = AuthenticatedUser.getUserId();
        
        var route = new TripRoute(
            routeResponse.distanceMeters(),
            routeResponse.durationSeconds(),
            new TripRoute.Geometry(
                routeResponse.geometry().type(),
                routeResponse.geometry().coordinates()
            )
        );
        var trip = new Trip(
            new ObjectId().toHexString(),
            request.from(),
            request.to(),
            riderId,
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

    public void rejectTrip(String tripId) {
        Trip trip = getTrip(tripId);
        String riderId = AuthenticatedUser.getUserId();
        if (!riderId.equals(trip.getRiderId())) {
            throw new IllegalArgumentException("Rider does not own this trip");
        }
        if (trip.getStatus() != TripStatus.REQUESTED) {
            throw new IllegalStateException("Trip is no longer requested");
        }
        tripRepository.deleteById(tripId);
    }

    public Trip assignDriver(String tripId, String driverId) {
        var trip = getTrip(tripId);
        trip.requireMatchingDriver(driverId);
        matchingService.accept(tripId, driverId);
        matchingService.confirm(tripId, driverId);
        trip.assignDriver(driverId);
        return tripRepository.save(trip);
    }

    public Trip assignAcceptedDriver(String tripId, String driverId) {
        Trip trip = getTrip(tripId);
        trip.assignDriver(driverId);
        return tripRepository.save(trip);
    }

    @Transactional
    public Trip confirmTrip(String tripId) {

    // 1. Find the trip
    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() ->
                    new IllegalArgumentException("Trip not found")
            );

    // 2. Validate and change the trip state
    trip.startMatching(); // Implement this domain operation according to your state machine.

    // 3. Save updated trip
    Trip savedTrip = tripRepository.save(trip);

    // 4. Create event payload
    Map<String, Object> payload = Map.of(
            "eventId", UUID.randomUUID().toString(),
            "tripId", savedTrip.getId(),
            "riderId", savedTrip.getRiderId(),
            "from", savedTrip.getFrom(),
            "to", savedTrip.getTo(),
            "occurredAt", Instant.now().toString()
    );

    // 5. Save event to outbox
    OutboxDocument event = OutboxDocument.builder()
            .id(UUID.randomUUID().toString())
            .aggregateType("Trip")
            .aggregateId(savedTrip.getId())
            .eventType("TripConfirmed")
            .payload(payload)
            .status("PENDING")
            .createdAt(Instant.now())
            .retryCount(0)
            .build();

    outboxRepository.save(event);

    return savedTrip;
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
        Trip savedTrip = tripRepository.save(trip);

        Map<String, Object> payload = Map.of(
                "eventId", UUID.randomUUID().toString(),
                "tripId", savedTrip.getId(),
                "riderId", savedTrip.getRiderId(),
                "driverId", savedTrip.getDriverId(),
                "amount", savedTrip.getPrice().longValue(),
                "currency", "VND",
                "quoteId", "",
                "occurredAt", Instant.now().toString()
        );
        outboxRepository.save(OutboxDocument.builder()
                .id(UUID.randomUUID().toString())
                .aggregateType("Trip")
                .aggregateId(savedTrip.getId())
                .eventType("TripCompleted")
                .payload(payload)
                .status("PENDING")
                .createdAt(Instant.now())
                .retryCount(0)
                .build());
        return savedTrip;
    }

    public Trip pickedUp(String tripId, String driverId) {
        Trip trip = getTrip(tripId);
        trip.pickedUp(driverId);
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
