package com.mircroservice.matching_service.domain.value_objects;

public record Position(Double latitude, Double longitude) {
    public Position {
        if (latitude < -90  || latitude > 90) {
            throw new IllegalArgumentException("latitude must be in range [-90,90]");
        }
        if (longitude < -180  || longitude > 180) {
            throw new IllegalArgumentException("longitude must be in range [-180,180]");
        }
    }
}
