package com.mircroservice.trip_service.domain.value_objects;

import java.util.Objects;

public record Position(Double latitude, Double longitude) {
    public Position {
        Objects.requireNonNull(latitude, "latitude must not be null");
        Objects.requireNonNull(longitude, "longitude must not be null");
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("latitude must be in range [-90,90]");
        }
        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("longitude must be in range [-180,180]");
        }
    }
}
