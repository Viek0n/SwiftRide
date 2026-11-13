package com.mircroservice.matching_service.domain.dto.responses;

import java.time.Instant;

import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

public record UserResponse(
    String id,
    String name,
    String phone,
    AccountStatus accountStatus,
    Instant createdAt,
    Instant updatedAt
) {
}
