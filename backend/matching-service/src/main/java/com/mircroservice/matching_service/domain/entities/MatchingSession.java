package com.mircroservice.matching_service.domain.entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.mircroservice.matching_service.domain.constants.MatchingConstant;
import com.mircroservice.matching_service.domain.enumerators.MatchingStatus;
import com.mircroservice.matching_service.domain.value_objects.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class MatchingSession {

    private String id;
    private String tripId;
    private Position riderPosition;

    private int attempt;
    private int maxAttempts;

    private Set<String> excludedDriverIds;

    private String currentDriverId;
    private String currentOfferId;

    private MatchingStatus status;

    private Instant createdAt;
    private Instant expiresAt;

    public MatchingSession(
        String tripId,
        Position riderPosition,
        int maxAttempts
    ) {
        this.tripId = tripId;
        this.riderPosition = riderPosition;
        this.maxAttempts = maxAttempts;

        this.attempt = 0;
        this.excludedDriverIds = new HashSet<>();
        this.status = MatchingStatus.SEARCHING;
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plus(MatchingConstant.MATCHING_LIVE_DURATION);
    }

    public void startWaiting(
        String driverId,
        String offerId
    ) {
        this.attempt++;
        this.currentDriverId = driverId;
        this.currentOfferId = offerId;
        this.status = MatchingStatus.WAITING_FOR_DRIVER;
    }

    public void excludeCurrentDriver() {
        excludedDriverIds.add(currentDriverId);
        clearCurrentOffer();
        status = MatchingStatus.SEARCHING;
    }

    public void clearCurrentOffer() {
        currentDriverId = null;
        currentOfferId = null;
        expiresAt = null;
    }

    public void markMatched() {
        status = MatchingStatus.MATCHED;
    }

    public void fail() {
        status = MatchingStatus.FAILED;
    }
}
