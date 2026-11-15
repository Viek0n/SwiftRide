package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.entities.MatchingAttempt;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.MatchingAttemptDocument;

@Component
public class MatchingAttemptDocumentMapper {
    public MatchingAttemptDocument toDocument(MatchingAttempt attempt) {
        MatchingAttemptDocument document = new MatchingAttemptDocument();
        document.setTripId(attempt.getTripId());
        document.setUserId(attempt.getUserId());
        document.setDriverId(attempt.getDriverId());
        return document;
    }

    public MatchingAttempt toDomain(MatchingAttemptDocument document) {
        return new MatchingAttempt(
            document.getTripId(),
            document.getUserId(),
            document.getDriverId()
        );
    }
}
