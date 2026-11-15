package com.mircroservice.matching_service.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

class GetNearbyActiveDriversUseCaseTest {
    @Test
    void returnsEligibleDriversWithinFiveKilometersInDistanceOrder() {
        ActiveDriverRepository activeDriverRepository = mock(ActiveDriverRepository.class);
        DriverProfileRepository driverProfileRepository = mock(DriverProfileRepository.class);
        ActiveDriver nearest = activeDriver("nearest", 0.005);
        ActiveDriver farther = activeDriver("farther", 0.02);
        ActiveDriver outsideRadius = activeDriver("outside", 0.06);
        ActiveDriver deletedUser = activeDriver("deleted", 0.001);

        when(activeDriverRepository.findByActiveStatus(ActiveStatus.ONLINE))
            .thenReturn(List.of(farther, outsideRadius, deletedUser, nearest));
        when(driverProfileRepository.findAll()).thenReturn(List.of(
            driverProfile("nearest", AccountStatus.ACTIVE),
            driverProfile("farther", AccountStatus.ACTIVE),
            driverProfile("outside", AccountStatus.ACTIVE),
            driverProfile("deleted", AccountStatus.ACTIVE)
        ));
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findAll()).thenReturn(List.of(
            user("nearest", AccountStatus.ACTIVE),
            user("farther", AccountStatus.ACTIVE),
            user("outside", AccountStatus.ACTIVE),
            user("deleted", AccountStatus.SOFT_DELETE)
        ));

        List<ActiveDriver> nearby = new GetNearbyActiveDriversUseCase(
            activeDriverRepository,
            driverProfileRepository,
            userRepository
        ).execute(new Position(0.0, 0.0));

        assertEquals(List.of(nearest, farther), nearby);
    }

    private ActiveDriver activeDriver(String userId, double longitude) {
        return new ActiveDriver(userId, ActiveStatus.ONLINE, new Position(0.0, longitude));
    }

    private DriverProfile driverProfile(String userId, AccountStatus status) {
        return new DriverProfile(userId, "license", "plate", status, null, null);
    }

    private User user(String id, AccountStatus status) {
        return new User(id, "name", id, "password", status, null, null);
    }
}
