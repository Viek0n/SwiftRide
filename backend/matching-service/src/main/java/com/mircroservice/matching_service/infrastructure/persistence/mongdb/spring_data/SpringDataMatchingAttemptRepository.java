package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.MatchingAttemptDocument;

public interface SpringDataMatchingAttemptRepository
    extends MongoRepository<MatchingAttemptDocument, String> {
    List<MatchingAttemptDocument> findByTripId(String tripId);

    Optional<MatchingAttemptDocument> findByTripIdAndDriverId(String tripId, String driverId);
}
