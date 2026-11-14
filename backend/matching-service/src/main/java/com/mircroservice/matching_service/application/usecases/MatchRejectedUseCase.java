package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;

@Service
public class MatchRejectedUseCase {
    private final ActiveDriverRepository activeDriverRepository;

    public MatchRejectedUseCase(ActiveDriverRepository activeDriverRepository) {
        this.activeDriverRepository = activeDriverRepository;
    }

    public ActiveDriver execute(String userId) {
        ActiveDriver driver = activeDriverRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy trạng thái tài xế"));

        if (driver.getActiveStatus() == ActiveStatus.ONLINE) {
            return driver;
        }
        if (driver.getActiveStatus() != ActiveStatus.MATCH_FOUND
            && driver.getActiveStatus() != ActiveStatus.MATCHING) {
            throw new IllegalStateException("Không thể từ chối yêu cầu ở trạng thái hiện tại");
        }

        driver.setActiveStatus(ActiveStatus.ONLINE);
        return activeDriverRepository.save(driver);
    }
}
