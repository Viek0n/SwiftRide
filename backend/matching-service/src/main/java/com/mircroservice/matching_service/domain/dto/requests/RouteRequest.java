package com.mircroservice.matching_service.domain.dto.requests;

import com.mircroservice.matching_service.domain.value_objects.Position;

public record RouteRequest(Position origin, Position destination) {}
