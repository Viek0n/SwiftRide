package com.mircroservice.matching_service.application.usecases.matching;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;

@Service
public class DriverStartMatchingUseCase {
    private final DriverProfileRepository driverProfileRepository;
    private final ActiveDriverRepository activeDriverRepository;

    public DriverStartMatchingUseCase(
        DriverProfileRepository driverProfileRepository,
        ActiveDriverRepository activeDriverRepository
    ) {
        this.driverProfileRepository = driverProfileRepository;
        this.activeDriverRepository = activeDriverRepository;
    }

    public Boolean execute(String userId) {
        DriverProfile profile = driverProfileRepository.findByUserId(userId).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy tài xế"));
        if (profile.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Hồ sơ tài xế không hoạt động");
        }

        ActiveDriver activeDriver = activeDriverRepository.findByUserId(userId).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy trạng thái tài xế"));
        
        activeDriver.startMatching();
        return activeDriverRepository.updateStatus(userId, activeDriver.getActiveStage());
    }
}
