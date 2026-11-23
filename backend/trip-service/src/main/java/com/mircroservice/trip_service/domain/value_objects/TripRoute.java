package com.mircroservice.trip_service.domain.value_objects;

import java.util.List;

public record TripRoute(
    double distanceMeters,
    double durationSeconds,
    Geometry geometry
) {
    public record Geometry(String type, List<List<Double>> coordinates) {}
}
