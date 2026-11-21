package com.mircroservice.matching_service.domain.dto.requests;

import com.mircroservice.matching_service.domain.value_objects.Position;

public record MatchRequest(
    String tripId, 
    Position origin
) {}
