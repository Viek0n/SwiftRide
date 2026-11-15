package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

@Service
public class DriverMatchingUseCase {
    private final DriverProfileRepository driverProfileRepository;
    private final ActiveDriverRepository activeDriverRepository;

    public DriverMatchingUseCase(
        DriverProfileRepository driverProfileRepository,
        ActiveDriverRepository activeDriverRepository
    ) {
        this.driverProfileRepository = driverProfileRepository;
        this.activeDriverRepository = activeDriverRepository;
    }

    public ActiveDriver execute(String userId, Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Vị trí tài xế không được để trống");
        }

        DriverProfile profile = driverProfileRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy tài xế"));
        if (profile.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Hồ sơ tài xế không hoạt động");
        }

        ActiveDriver activeDriver = activeDriverRepository.findById(userId)
            .orElseGet(() -> new ActiveDriver(userId, ActiveStatus.ONLINE, position));
        if (activeDriver.getActiveStatus() == ActiveStatus.MATCH_FOUND
            || activeDriver.getActiveStatus() == ActiveStatus.IN_MATCH) {
            throw new IllegalStateException("Tài xế đang tham gia một chuyến đi");
        }

        activeDriver.setActiveStatus(ActiveStatus.ONLINE);
        activeDriver.setActivePosition(position);
        return activeDriverRepository.save(activeDriver);
    }
}
