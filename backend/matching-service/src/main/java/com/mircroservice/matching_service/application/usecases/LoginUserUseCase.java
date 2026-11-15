package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.application.abstractions.PasswordHasher;
import com.mircroservice.matching_service.domain.dto.requests.LoginRequest;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@Service
public class LoginUserUseCase {
    private static final String INVALID_CREDENTIALS = "Số điện thoại hoặc mật khẩu không chính xác";

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public LoginUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public UserResponse execute(LoginRequest request) {
        User user = userRepository.findByPhone(request.phone()).orElseThrow(
            () -> new IllegalArgumentException(INVALID_CREDENTIALS));
        if (user.getAccountStatus() != AccountStatus.ACTIVE
            || !passwordHasher.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS);
        }
        return UserResponse.toResponse(user);
    }
}
