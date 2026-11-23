package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import java.time.Instant;
import java.util.Set;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.domain.constants.MatchingConstant;
import com.mircroservice.matching_service.domain.entities.MatchingSession;
import com.mircroservice.matching_service.domain.value_objects.Position;
import com.mircroservice.matching_service.infrastructure.persistence.mongdb.documents.MatchingSessionDocument;

@Component 
public class MatchingSessionMapper {
    public MatchingSessionDocument toDocument(MatchingSession offer) {
        MatchingSessionDocument document = new MatchingSessionDocument();
        document.setId(offer.getId());
        document.setTripId(offer.getTripId());
        Position position = offer.getRiderPosition();
        document.setRiderPosition(new GeoJsonPoint(position.longitude(), position.latitude()));
        document.setAttempt(offer.getAttempt());
        document.setMaxAttempts(MatchingConstant.MAX_MATCHING_ATTEMPT);
        document.setExcludedDriverIds(offer.getExcludedDriverIds());
        document.setCurrentDriverId(offer.getCurrentDriverId());
        document.setCurrentOfferId(offer.getCurrentOfferId());
        document.setStatus(offer.getStatus());
        document.setCreatedAt(offer.getCreatedAt());
        document.setExpiresAt(offer.getExpiresAt());
        return document;
    }

    public MatchingSession toDomain(MatchingSessionDocument document) {
        GeoJsonPoint point = document.getRiderPosition();
        Position position = new Position(point.getX(), point.getY());
        return new MatchingSession(
            document.getId(),
            document.getTripId(),
            position,
            document.getAttempt(),
            document.getMaxAttempts(),
            document.getExcludedDriverIds(),
            document.getCurrentDriverId(),
            document.getCurrentOfferId(),
            document.getStatus(),
            document.getCreatedAt(),
            document.getExpiresAt()
        );
    }
}
