package com.mircroservice.matching_service.application.abstractions;

public interface PasswordHasher {
    String hash(String rawPassword);
}
