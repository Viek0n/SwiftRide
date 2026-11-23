package com.mircroservice.matching_service.infrastructure.persistence.mongdb.spring_data;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.MatchingSessionDocument;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SpringDataMatchingSessionMongoRepository
        extends MongoRepository<MatchingSessionDocument, String> {
    Optional<MatchingSessionDocument> findByTripId(String tripId);
    Optional<MatchingSessionDocument> findByCurrentOfferId(String offerId);
    List<MatchingSessionDocument> findByExpiresAtBeforeAndStatus(
            Instant expiresAt,
            com.mircroservice.matching_service.domain.enumerators.MatchingStatus status);
}
