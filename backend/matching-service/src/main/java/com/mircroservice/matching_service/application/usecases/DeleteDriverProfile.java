package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;

@Service
public class DeleteDriverProfile {
    private final DriverProfileRepository driverProfileRepository;
    private final ActiveDriverRepository activeDriverRepository;

    public DeleteDriverProfile(
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

        activeDriverRepository.deleteById(userId);
        driverProfileRepository.deleteById(userId);
    }
}
