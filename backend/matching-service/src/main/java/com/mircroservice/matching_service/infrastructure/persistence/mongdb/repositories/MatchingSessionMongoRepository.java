package com.mircroservice.matching_service.infrastructure.persistence.mongdb.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.MatchingSession;
import com.mircroservice.matching_service.domain.repositories.MatchingSessionRepository;

public class MatchingSessionMongoRepository implements MatchingSessionRepository{

    @Override
    public MatchingSession save(MatchingSession session) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public Optional<MatchingSession> findById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Optional<MatchingSession> findByTripId(String tripId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByTripId'");
    }

    @Override
    public Optional<MatchingSession> findByCurrentOfferId(String offerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByCurrentOfferId'");
    }

    @Override
    public boolean startWaitingIfSearching(String sessionId, String driverId, String offerId, Instant expiresAt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startWaitingIfSearching'");
    }

    @Override
    public boolean markMatchedIfWaiting(String offerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'markMatchedIfWaiting'");
    }

    @Override
    public boolean markSearchingIfWaiting(String offerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'markSearchingIfWaiting'");
    }

    @Override
    public List<MatchingSession> findExpiredSessions(Instant now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findExpiredSessions'");
    }
    
}
