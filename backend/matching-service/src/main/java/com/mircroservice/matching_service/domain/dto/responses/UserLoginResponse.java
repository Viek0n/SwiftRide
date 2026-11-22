package com.mircroservice.matching_service.domain.dto.responses;

public record UserLoginResponse(
    String accessToken,
    UserResponse user
) {}
