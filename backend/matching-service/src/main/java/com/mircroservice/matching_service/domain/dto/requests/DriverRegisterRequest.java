package com.mircroservice.matching_service.domain.dto.requests;

public record DriverRegisterRequest(
    String userId,
    String driverLicense,
    String licensePlate
) {}
