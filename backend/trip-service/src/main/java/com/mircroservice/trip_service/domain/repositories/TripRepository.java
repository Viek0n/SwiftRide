package com.mircroservice.trip_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.mircroservice.trip_service.domain.entities.Trip;

public interface TripRepository {
    Trip save(Trip trip);
    Optional<Trip> findById(String id);
    List<Trip> findByRiderId(String riderId);
}