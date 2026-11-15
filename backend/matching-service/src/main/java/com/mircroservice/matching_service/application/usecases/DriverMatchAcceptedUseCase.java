package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;

@Service
public class DriverMatchAcceptedUseCase {
    private final ActiveDriverRepository activeDriverRepository;

    public DriverMatchAcceptedUseCase(ActiveDriverRepository activeDriverRepository) {
        this.activeDriverRepository = activeDriverRepository;
    }

    public ActiveDriver execute(String userId) {
        ActiveDriver driver = activeDriverRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy trạng thái tài xế"));

        if (driver.getActiveStatus() == ActiveStatus.IN_MATCH) {
            return driver;
        }
        if (driver.getActiveStatus() != ActiveStatus.MATCH_FOUND) {
            throw new IllegalStateException("Không có yêu cầu ghép chuyến đang chờ");
        }

        driver.setActiveStatus(ActiveStatus.IN_MATCH);
        return activeDriverRepository.save(driver);
    }
}
