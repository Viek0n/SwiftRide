package com.mircroservice.matching_service.domain.repositories;

import java.util.Optional;
import java.util.List;

import com.mircroservice.matching_service.domain.entities.MatchingAttempt;

public interface MatchingAttemptRepository {
    MatchingAttempt save(MatchingAttempt attempt);

    List<MatchingAttempt> findByTripId(String tripId);

    Optional<MatchingAttempt> findByTripIdAndDriverId(String tripId, String driverId);
}
