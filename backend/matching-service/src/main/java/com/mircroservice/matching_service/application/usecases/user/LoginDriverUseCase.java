package com.mircroservice.matching_service.application.usecases.user;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.application.abstractions.PasswordHasher;
import com.mircroservice.matching_service.application.security.JwtTokenService;
import com.mircroservice.matching_service.domain.dto.requests.LoginRequest;
import com.mircroservice.matching_service.domain.dto.responses.DriverLoginResponse;
import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;
import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@Service
public class LoginDriverUseCase {
    private static final String INVALID_CREDENTIALS = "Số điện thoại hoặc mật khẩu không chính xác";

    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final PasswordHasher passwordHasher;
    private final JwtTokenService jwtTokenService;

    public LoginDriverUseCase(
        UserRepository userRepository,
        DriverProfileRepository driverProfileRepository,
        PasswordHasher passwordHasher,
        JwtTokenService jwtTokenService
    ) {
        this.userRepository = userRepository;
        this.driverProfileRepository = driverProfileRepository;
        this.passwordHasher = passwordHasher;
        this.jwtTokenService = jwtTokenService;
    }

    public DriverLoginResponse execute(LoginRequest request) {
        User user = userRepository.findByPhone(request.phone()).orElseThrow(
            () -> new IllegalArgumentException(INVALID_CREDENTIALS));
        if (user.getAccountStatus() != AccountStatus.ACTIVE
            || !passwordHasher.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS);
        }

        DriverProfile driver = driverProfileRepository.findById(user.getId()).orElseThrow(
            () -> new IllegalArgumentException(INVALID_CREDENTIALS));
        if (driver.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS);
        }

        return new DriverLoginResponse(
            jwtTokenService.createToken(user, "DRIVER"),
            UserResponse.toResponse(user),
            DriverProfileResponse.toResponse(driver)
        );
    }
}
