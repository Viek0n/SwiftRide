package com.mircroservice.matching_service.domain.exceptions.stages;

public class DriverNotAvailableException
        extends DriverStateException {

    public DriverNotAvailableException(String driverId) {
        super("Driver is not available: " + driverId);
    }
}
