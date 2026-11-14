package com.mircroservice.matching_service.application.usecases;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

@Service
public class GetNearbyActiveDriversUseCase {
    private static final double MAX_DISTANCE_METERS = 5_000.0;
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private final ActiveDriverRepository activeDriverRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final UserRepository userRepository;

    public GetNearbyActiveDriversUseCase(
        ActiveDriverRepository activeDriverRepository,
        DriverProfileRepository driverProfileRepository,
        UserRepository userRepository
    ) {
        this.activeDriverRepository = activeDriverRepository;
        this.driverProfileRepository = driverProfileRepository;
        this.userRepository = userRepository;
    }

    public List<ActiveDriver> execute(Position riderPosition) {
        if (riderPosition == null) {
            throw new IllegalArgumentException("Vị trí hành khách không được để trống");
        }

        Set<String> activeUserIds = userRepository.findAll().stream()
            .filter(user -> user.getAccountStatus() == AccountStatus.ACTIVE)
            .map(User::getId)
            .collect(Collectors.toSet());
        Set<String> activeDriverIds = driverProfileRepository.findAll().stream()
            .filter(driver -> driver.getAccountStatus() == AccountStatus.ACTIVE)
            .filter(driver -> activeUserIds.contains(driver.getUserId()))
            .map(driver -> driver.getUserId())
            .collect(Collectors.toSet());

        return activeDriverRepository.findByActiveStatus(ActiveStatus.ONLINE).stream()
            .filter(driver -> activeDriverIds.contains(driver.getUserId()))
            .filter(driver -> driver.getActivePosition() != null)
            .filter(driver -> distanceMeters(riderPosition, driver.getActivePosition()) <= MAX_DISTANCE_METERS)
            .sorted(Comparator.comparingDouble(
                driver -> distanceMeters(riderPosition, driver.getActivePosition())))
            .toList();
    }

    private double distanceMeters(Position first, Position second) {
        double firstLatitude = Math.toRadians(first.latitude());
        double secondLatitude = Math.toRadians(second.latitude());
        double latitudeDifference = secondLatitude - firstLatitude;
        double longitudeDifference = Math.toRadians(second.longitude() - first.longitude());

        double haversine = Math.pow(Math.sin(latitudeDifference / 2), 2)
            + Math.cos(firstLatitude) * Math.cos(secondLatitude)
            * Math.pow(Math.sin(longitudeDifference / 2), 2);

        double normalizedHaversine = Math.min(1.0, Math.max(0.0, haversine));
        return 2 * EARTH_RADIUS_METERS * Math.asin(Math.sqrt(normalizedHaversine));
    }
}
