package com.mircroservice.matching_service.application.usecases;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.application.abstractions.PasswordHasher;
import com.mircroservice.matching_service.domain.dto.requests.UserRegisterRequest;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@Service
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public UserResponse execute(UserRegisterRequest request) {
        if (userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Số điện thoại đã đăng kí");
        }

        Instant now = Instant.now();
        User user = new User(
            null,
            request.name(),
            request.phone(),
            passwordHasher.hash(request.password()),
            AccountStatus.ACTIVE,
            now,
            now
        );

        User savedUser = userRepository.save(user);
        return UserResponse.toResponse(savedUser);
    }
}
