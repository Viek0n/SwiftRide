package com.mircroservice.matching_service.domain.exceptions.stages;

public class DriverStateException extends IllegalStateException {
    public DriverStateException(String message) {
        super(message);
    }
}