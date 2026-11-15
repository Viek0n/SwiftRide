package com.mircroservice.matching_service.application.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.mircroservice.matching_service.application.abstractions.PasswordHasher;

@Component 
public class BCryptPasswordHasher implements PasswordHasher{
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
