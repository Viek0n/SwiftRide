package com.mircroservice.trip_service.infrastructure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mircroservice.trip_service.domain.entities.Trip;
import com.mircroservice.trip_service.domain.repositories.TripRepository;

@Repository
public class TripMongoRepository implements TripRepository{
    private final TripSpringDataRepository mongoRepository;

    public TripMongoRepository(TripSpringDataRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Trip save(Trip trip) {
        return mongoRepository.save(trip);
    }

    @Override
    public Optional<Trip> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public List<Trip> findByRiderId(String riderId) {
        return mongoRepository.findByRiderId(riderId);
    }
}
