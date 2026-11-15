package com.mircroservice.matching_service.domain.value_objects;

import com.fasterxml.jackson.annotation.JsonAlias;

public record Position(
    @JsonAlias("lat") Double latitude,
    @JsonAlias({"lon", "lng"}) Double longitude
) {
    public Position {
        if (latitude == null || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("latitude must be in range [-90,90]");
        }
        if (longitude == null || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("longitude must be in range [-180,180]");
        }
    }
}
