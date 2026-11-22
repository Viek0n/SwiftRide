package com.mircroservice.trip_service.infrastructure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mircroservice.trip_service.domain.entities.Trip;
import com.mircroservice.trip_service.domain.repositories.TripRepository;
import com.mircroservice.trip_service.infrastructure.documents.TripDocument;

@Repository
public class TripMongoRepository implements TripRepository {

    private final TripSpringDataRepository mongoRepository;
    private final TripDocumentMapper mapper;

    public TripMongoRepository(
            TripSpringDataRepository mongoRepository,
            TripDocumentMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public Trip save(Trip trip) {
        TripDocument document = mapper.toDocument(trip);
        TripDocument saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Trip> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Trip> findByRiderId(String riderId) {
        return mongoRepository.findByRiderId(riderId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}
