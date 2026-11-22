package com.mircroservice.matching_service.application.usecases.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mircroservice.matching_service.application.abstractions.PasswordHasher;
import com.mircroservice.matching_service.application.security.JwtTokenService;
import com.mircroservice.matching_service.domain.dto.requests.LoginRequest;
import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoginDriverUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private DriverProfileRepository driverProfileRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private LoginDriverUseCase loginDriver;

    @Test
    void returnsDriverProfileAndJwtAfterSuccessfulLogin() {
        User user = new User();
        user.setId("driver-123");
        user.setName("Test Driver");
        user.setPhone("5551234");
        user.setPassword("hashed-password");
        user.setAccountStatus(AccountStatus.ACTIVE);

        DriverProfile driver = new DriverProfile();
        driver.setUserId(user.getId());
        driver.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findByPhone(user.getPhone())).thenReturn(Optional.of(user));
        when(passwordHasher.matches("password", user.getPassword())).thenReturn(true);
        when(driverProfileRepository.findById(user.getId())).thenReturn(Optional.of(driver));
        when(jwtTokenService.createToken(user, "DRIVER")).thenReturn("signed-driver-token");

        var response = loginDriver.execute(new LoginRequest(user.getPhone(), "password"));

        assertThat(response.accessToken()).isEqualTo("signed-driver-token");
        assertThat(response.user().id()).isEqualTo(user.getId());
        assertThat(response.driver().userId()).isEqualTo(user.getId());
        verify(jwtTokenService).createToken(user, "DRIVER");
    }
}
