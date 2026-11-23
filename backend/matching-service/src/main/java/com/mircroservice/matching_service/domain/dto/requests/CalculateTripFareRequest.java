package com.mircroservice.matching_service.domain.dto.requests;

import com.mircroservice.matching_service.domain.value_objects.Position;

public record CalculateTripFareRequest(
    Position origin,
    Position destination,
    Boolean surgeApplied,
    Boolean promoApplied
) {
    public CalculateTripFareRequest {
        surgeApplied = Boolean.TRUE.equals(surgeApplied);
        promoApplied = Boolean.TRUE.equals(promoApplied);
    }
}
