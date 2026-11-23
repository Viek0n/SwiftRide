package com.mircroservice.matching_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.TripOffer;

public interface TripOfferRepository {
    TripOffer save(TripOffer offer);

    Optional<TripOffer> findById(String tripId);

    Optional<TripOffer> findByTripId(String tripId);

    List<TripOffer> getAllByTripId(String tripId);

    List<TripOffer> getAllBydDriverId(String tripId);

    boolean markAcceptedIfPending(String offerId);

    boolean markRejectedIfPending(String offerId);

    boolean markExpiredIfPending(String offerId);
}