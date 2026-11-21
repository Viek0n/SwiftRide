package com.mircroservice.matching_service.domain.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.MatchingSession;

public interface MatchingSessionRepository {

    MatchingSession save(MatchingSession session);

    Optional<MatchingSession> findById(String id);

    Optional<MatchingSession> findByTripId(String tripId);

    Optional<MatchingSession> findByCurrentOfferId(String offerId);

    boolean startWaitingIfSearching(
        String sessionId,
        String driverId,
        String offerId,
        Instant expiresAt
    );

    boolean markMatchedIfWaiting(String offerId);

    boolean markSearchingIfWaiting(String offerId);

    List<MatchingSession> findExpiredSessions(Instant now);
}