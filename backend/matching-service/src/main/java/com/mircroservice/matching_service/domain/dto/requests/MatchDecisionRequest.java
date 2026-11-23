package com.mircroservice.matching_service.domain.dto.requests;

public record MatchDecisionRequest(
    String tripId, 
    String driverId
) {}
