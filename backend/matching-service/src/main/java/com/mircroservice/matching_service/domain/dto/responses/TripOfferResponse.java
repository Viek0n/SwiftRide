package com.mircroservice.matching_service.domain.dto.responses;

import com.mircroservice.matching_service.domain.enumerators.OfferStatus;

public record TripOfferResponse(
    String offerId,
    String tripId,
    String driverId,
    OfferStatus status
) {}
