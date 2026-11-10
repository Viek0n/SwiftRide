package com.mircroservice.trip_service.domain.entities;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mircroservice.trip_service.domain.enums.TripStatus;
import com.mircroservice.trip_service.domain.states.TripState;
import com.mircroservice.trip_service.domain.value_objects.Position;

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
    private Integer price;
    private String riderId;
    private String driverId;
    private Instant createdAt;

    public Trip(String id, Position from, Position to, String riderId, Instant createdAt) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.status = TripStatus.MATCHING;
        this.riderId = riderId;
        this.createdAt = createdAt;
    }

    public void assignDriver(String driverId) {
        var nextState = currentState().assignDriver(driverId);
        this.driverId = driverId;
        transitionTo(nextState);
    }

    public void markPickedUp() {
        transitionTo(currentState().markPickedUp());
    }

    public void complete() {
        transitionTo(currentState().complete());
    }

    public void cancel() {
        transitionTo(currentState().cancel());
    }

    public void markMatchingFailed() {
        transitionTo(currentState().markMatchingFailed());
    }

    private TripState currentState() {
        return TripState.from(status);
    }

    private void transitionTo(TripState nextState) {
        this.status = nextState.getStatus();
    }
}
