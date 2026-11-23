package com.mircroservice.matching_service.application.abstractions;

import java.util.List;
import java.util.Objects;

import com.mircroservice.matching_service.domain.value_objects.Position;

public interface RouteInfoProvider {
    RouteInfo getRoute(Position origin, Position destination);

    record RouteInfo(
        double distanceMeters,
        double durationSeconds,
        List<List<Double>> geometryCoordinates
    ) {
        public RouteInfo(double distanceMeters, double durationSeconds) {
            this(distanceMeters, durationSeconds, List.of());
        }

        public RouteInfo {
            if (!Double.isFinite(distanceMeters) || distanceMeters < 0) {
                throw new IllegalArgumentException("Route distance must be finite and non-negative");
            }
            if (!Double.isFinite(durationSeconds) || durationSeconds < 0) {
                throw new IllegalArgumentException("Route duration must be finite and non-negative");
            }
            Objects.requireNonNull(geometryCoordinates, "Route geometry coordinates are required");
            geometryCoordinates = geometryCoordinates.stream()
                .map(List::copyOf)
                .toList();
        }
    }
}
