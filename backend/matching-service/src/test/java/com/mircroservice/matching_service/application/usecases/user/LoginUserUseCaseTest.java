package com.mircroservice.matching_service.application.usecases.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
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
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private LoginUserUseCase loginUser;

    @Test
    void returnsUserAndJwtAfterSuccessfulLogin() {
        User user = activeUser();
        when(userRepository.findByPhone("5551234")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("password", "hashed-password")).thenReturn(true);
        when(jwtTokenService.createToken(user)).thenReturn("signed-token");

        var response = loginUser.execute(new LoginRequest("5551234", "password"));

        assertThat(response.accessToken()).isEqualTo("signed-token");
        assertThat(response.user().id()).isEqualTo("user-123");
        verify(jwtTokenService).createToken(user);
    }

    @Test
    void rejectsInvalidPasswordWithoutCreatingJwt() {
        User user = activeUser();
        when(userRepository.findByPhone("5551234")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> loginUser.execute(new LoginRequest("5551234", "wrong-password")))
            .isInstanceOf(IllegalArgumentException.class);
        verify(jwtTokenService, never()).createToken(user);
    }

    private User activeUser() {
        User user = new User();
        user.setId("user-123");
        user.setName("Test User");
        user.setPhone("5551234");
        user.setPassword("hashed-password");
        user.setAccountStatus(AccountStatus.ACTIVE);
        return user;
    }
}
