package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

@Service
public class MatchingRideUseCase {
    private final GetNearbyActiveDriversUseCase getNearbyActiveDrivers;
    private final ActiveDriverRepository activeDriverRepository;

    public MatchingRideUseCase(
        GetNearbyActiveDriversUseCase getNearbyActiveDrivers,
        ActiveDriverRepository activeDriverRepository
    ) {
        this.getNearbyActiveDrivers = getNearbyActiveDrivers;
        this.activeDriverRepository = activeDriverRepository;
    }

    public ActiveDriver execute(Position riderPosition) {
        ActiveDriver matchedDriver = getNearbyActiveDrivers.execute(riderPosition).stream().findFirst()
            .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài xế phù hợp"));
        matchedDriver.setActiveStatus(ActiveStatus.MATCH_FOUND);
        return activeDriverRepository.save(matchedDriver);
    }
}
