package com.mircroservice.trip_service.infrastructure.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.trip_service.domain.entities.Trip;

public interface TripSpringDataRepository extends MongoRepository<Trip, String> {
    List<Trip> findByRiderId(String riderId);
}
