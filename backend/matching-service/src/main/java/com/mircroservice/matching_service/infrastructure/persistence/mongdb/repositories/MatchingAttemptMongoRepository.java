package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mircroservice.matching_service.domain.entities.MatchingAttempt;
import com.mircroservice.matching_service.domain.repositories.MatchingAttemptRepository;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers.MatchingAttemptDocumentMapper;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data.SpringDataMatchingAttemptRepository;

@Repository
public class MatchingAttemptMongoRepository implements MatchingAttemptRepository {
    private final SpringDataMatchingAttemptRepository mongoRepository;
    private final MatchingAttemptDocumentMapper mapper;

    public MatchingAttemptMongoRepository(
        SpringDataMatchingAttemptRepository mongoRepository,
        MatchingAttemptDocumentMapper mapper
    ) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public MatchingAttempt save(MatchingAttempt attempt) {
        return mapper.toDomain(mongoRepository.save(mapper.toDocument(attempt)));
    }

    @Override
    public List<MatchingAttempt> findByTripId(String tripId) {
        return mongoRepository.findByTripId(tripId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<MatchingAttempt> findByTripIdAndDriverId(String tripId, String driverId) {
        return mongoRepository.findByTripIdAndDriverId(tripId, driverId)
            .map(mapper::toDomain);
    }
}
