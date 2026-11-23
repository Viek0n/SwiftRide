package com.mircroservice.matching_service.domain.dto.requests;

public record UserUpdateRequest(
    String name, 
    String phone
) {}
