package com.mircroservice.matching_service.domain.dto.responses;

import java.time.Instant;

import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

public record DriverProfileResponse(
    String userId,
    String driverLicense,
    String licensePlate,
    AccountStatus accountStatus,
    Instant createdAt,
    Instant updatedAt
) {
    public static DriverProfileResponse toResponse(DriverProfile driver) {
        return new DriverProfileResponse(
            driver.getUserId(),
            driver.getDriverLicense(),
            driver.getLicensePlate(),
            driver.getAccountStatus(),
            driver.getCreatedAt(),
            driver.getUpdatedAt()
        );
    }
}
