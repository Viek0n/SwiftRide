package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;

@Service
public class SoftDeleteDriverProfileUsecase {
    private final DriverProfileRepository driverProfileRepository;
    private final ActiveDriverRepository activeDriverRepository;

    public SoftDeleteDriverProfileUsecase(
        DriverProfileRepository driverProfileRepository,
        ActiveDriverRepository activeDriverRepository
    ) {
        this.driverProfileRepository = driverProfileRepository;
        this.activeDriverRepository = activeDriverRepository;
    }

    public void execute(String userId) {
        if (driverProfileRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy tài xế");
        }

        driverProfileRepository.softDeleteById(userId);
        activeDriverRepository.deleteById(userId);
    }
}
