package com.mircroservice.matching_service.domain.dto.responses;

import java.time.Instant;

import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

public record UserResponse(
    String id,
    String name,
    String phone,
    AccountStatus accountStatus,
    Instant createdAt,
    Instant updatedAt
) {
    public static UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getPhone(),
            user.getAccountStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
