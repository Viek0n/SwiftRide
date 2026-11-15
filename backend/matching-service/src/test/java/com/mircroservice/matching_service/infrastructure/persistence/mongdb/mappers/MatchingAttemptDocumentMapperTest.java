package com.mircroservice.matching_service.infrastructure.persistence.mongdb.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.mircroservice.matching_service.domain.entities.MatchingAttempt;

class MatchingAttemptDocumentMapperTest {
    private final MatchingAttemptDocumentMapper mapper = new MatchingAttemptDocumentMapper();

    @Test
    void mapsOnlyTripUserAndDriverToAttemptDocument() {
        var document = mapper.toDocument(new MatchingAttempt("trip-1", "rider-1", "driver-1"));

        assertNull(document.getId());
        assertEquals("trip-1", document.getTripId());
        assertEquals("rider-1", document.getUserId());
        assertEquals("driver-1", document.getDriverId());
    }

    @Test
    void mapsAttemptDocumentBackToDomain() {
        var document = mapper.toDocument(new MatchingAttempt("trip-1", "rider-1", "driver-1"));

        MatchingAttempt attempt = mapper.toDomain(document);
        assertEquals("trip-1", attempt.getTripId());
        assertEquals("rider-1", attempt.getUserId());
        assertEquals("driver-1", attempt.getDriverId());
    }
}
