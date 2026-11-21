package com.mircroservice.matching_service.domain.entities;

import java.time.Instant;
import java.time.LocalDateTime;

import com.mircroservice.matching_service.domain.constants.MatchingConstant;
import com.mircroservice.matching_service.domain.enumerators.OfferStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter     
@NoArgsConstructor 
@AllArgsConstructor 
public class TripOffer {
    private String id;
    private String tripId;
    private String driverId;

    private OfferStatus status;

    private Instant createdAt;
    private Instant expiresAt;

    public TripOffer(
        String tripId,
        String driverId
    ) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.status = OfferStatus.PENDING;
        this.createdAt = Instant.now();
        this.expiresAt = createdAt.plus(MatchingConstant.OFFER_LIVE_DURATION);
    }

    public boolean isExpired(){
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isPending(){
        return this.status == OfferStatus.PENDING;
    }
    
    public void accept() {
        if (status != OfferStatus.PENDING) {
            throw new IllegalStateException(
                "Offer is no longer pending"
            );
        }

        status = OfferStatus.ACCEPTED;
    }

    public void reject() {
        if (status != OfferStatus.PENDING) {
            throw new IllegalStateException(
                "Offer is no longer pending"
            );
        }

        status = OfferStatus.REJECTED;
    }

    public void expire() {
        if (status != OfferStatus.PENDING) {
            throw new IllegalStateException(
                "Offer is no longer pending"
            );
        }

        status = OfferStatus.EXPIRED;
    }

}