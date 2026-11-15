package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;
import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;

@Service
public class FindDriverByIdUseCase {
    private final DriverProfileRepository driverProfileRepository;

    public FindDriverByIdUseCase(DriverProfileRepository driverProfileRepository) {
        this.driverProfileRepository = driverProfileRepository;
    }

    public DriverProfileResponse execute(String userId) {
        DriverProfile driver = driverProfileRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy tài xế"));
        return DriverProfileResponse.toResponse(driver);
    }
}
