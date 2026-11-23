package com.mircroservice.matching_service.domain.dto.requests;

public record TripOfferRequest(
    String offerId,
    String tripId,
    String driverId,
    int attempt
) {}
