package com.mircroservice.matching_service.domain.dto.requests;

public record UserRegisterRequest(
    String name,
    String phone,
    String password
) {}