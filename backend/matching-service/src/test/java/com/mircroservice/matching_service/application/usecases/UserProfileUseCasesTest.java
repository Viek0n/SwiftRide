package com.mircroservice.matching_service.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mircroservice.matching_service.domain.dto.requests.UserUpdateRequest;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

class UserProfileUseCasesTest {
    @Test
    void updatesUserProfileAndReturnsSafeResponse() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = new User("user-1", "Before", "100", "hash", AccountStatus.ACTIVE, null, null);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = new UpdateUserUsecase(userRepository)
            .execute("user-1", new UserUpdateRequest("After", "200"));

        assertEquals("After", response.name());
        assertEquals("200", response.phone());
        verify(userRepository).save(user);
    }

    @Test
    void softDeletingUserAlsoDeactivatesDriverProfileAndPresence() {
        UserRepository userRepository = mock(UserRepository.class);
        DriverProfileRepository driverProfileRepository = mock(DriverProfileRepository.class);
        ActiveDriverRepository activeDriverRepository = mock(ActiveDriverRepository.class);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(
            new User("user-1", "Rider", "100", "hash", AccountStatus.ACTIVE, null, null)
        ));
        when(driverProfileRepository.existsById("user-1")).thenReturn(true);

        new SoftDeleteUserUseCase(
            userRepository,
            driverProfileRepository,
            activeDriverRepository
        ).execute("user-1");

        verify(userRepository).softDeleteById("user-1");
        verify(driverProfileRepository).softDeleteById("user-1");
        verify(activeDriverRepository).deleteById("user-1");
    }
}
