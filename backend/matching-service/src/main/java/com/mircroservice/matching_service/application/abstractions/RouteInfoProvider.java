package com.mircroservice.matching_service.application.abstractions;

import com.mircroservice.matching_service.domain.value_objects.Position;

public interface RouteInfoProvider {
    RouteInfo getRoute(Position origin, Position destination);

    record RouteInfo(double distanceMeters, double durationSeconds) {
        public RouteInfo {
            if (!Double.isFinite(distanceMeters) || distanceMeters < 0) {
                throw new IllegalArgumentException("Route distance must be finite and non-negative");
            }
            if (!Double.isFinite(durationSeconds) || durationSeconds < 0) {
                throw new IllegalArgumentException("Route duration must be finite and non-negative");
            }
        }
    }
}
