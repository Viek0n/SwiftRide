package com.mircroservice.matching_service.domain.dto.responses;

import java.util.List;

public record RouteResponse(
    double distanceMeters,
    double durationSeconds,
    Geometry geometry
) {
    public record Geometry(String type, List<List<Double>> coordinates) {}
}
