package com.mircroservice.matching_service.domain.dto.responses;

public record DriverLoginResponse(
    String accessToken,
    UserResponse user,
    DriverProfileResponse driver
) {}
