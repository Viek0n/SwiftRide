package com.mircroservice.trip_service.domain.entities;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mircroservice.trip_service.domain.enums.TripStatus;
import com.mircroservice.trip_service.domain.states.TripState;
import com.mircroservice.trip_service.domain.value_objects.Position;
import com.mircroservice.trip_service.domain.value_objects.TripRoute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "trips")
public class Trip {
    @Id
    private String id;
    private Position from;
    private Position to;
    private TripStatus status;
    private Double distance;
    private BigDecimal price;
    private TripRoute route;
    private String riderId;
    private String driverId;
    private Instant createdAt;

    public Trip(
        String id,
        Position from,
        Position to,
        String riderId,
        TripRoute route,
        BigDecimal price,
        Instant createdAt
    ) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.status = TripStatus.REQUESTED;
        this.riderId = riderId;
        this.route = route;
        this.distance = route.distanceMeters();
        this.price = price;
        this.createdAt = createdAt;
    }

    public void restoreState(TripStatus status, String driverId) {
        this.status = Objects.requireNonNull(status);
        this.driverId = driverId;
    }

    public void startMatching(String driverId) {
        requireDriverId(driverId);
        transitionTo(currentState().startMatching());
        this.driverId = driverId;
    }

    public void startMatching() {
        transitionTo(currentState().startMatching());
        this.driverId = null;
    }

    public void updateMatchingDriver(String driverId) {
        requireMatchingDriver();
        requireDriverId(driverId);
        this.driverId = driverId;
    }

    public void assignDriver(String driverId) {
        requireMatchingDriver();
        if (!Objects.equals(this.driverId, driverId)) {
            if (this.driverId != null) {
                throw new IllegalArgumentException("Driver was not asked to match this trip");
            }
        }
        var nextState = currentState().assignDriver(driverId);
        transitionTo(nextState);
        this.driverId = driverId;
    }

    public void complete(String driverId) {
        if (status != TripStatus.INPROGRESS) {
            throw new IllegalStateException("Trip is not in progress");
        }
        requireAssignedDriver(driverId);
        transitionTo(currentState().complete());
    }

    public void cancel() {
        transitionTo(currentState().cancel());
        this.driverId = null;
    }

    public void markMatchingFailed() {
        transitionTo(currentState().markMatchingFailed());
        this.driverId = null;
    }

    public void requireMatchingDriver(String driverId) {
        requireMatchingDriver();
        if (driverId == null || !driverId.equals(this.driverId)) {
            throw new IllegalArgumentException("Driver was not asked to match this trip");
        }
    }

    private TripState currentState() {
        return TripState.from(status);
    }

    private void transitionTo(TripState nextState) {
        this.status = nextState.getStatus();
    }

    private void requireMatchingDriver() {
        if (status != TripStatus.MATCHING) {
            throw new IllegalStateException("Trip is not waiting for a matching driver");
        }
    }

    private void requireAssignedDriver(String driverId) {
        requireDriverId(driverId);
        if (!Objects.equals(this.driverId, driverId)) {
            throw new IllegalArgumentException("Driver is not assigned to this trip");
        }
    }

    private void requireDriverId(String driverId) {
        if (driverId == null || driverId.isBlank()) {
            throw new IllegalArgumentException("driverId must not be blank");
        }
    }
}
