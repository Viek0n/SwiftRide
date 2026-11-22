package com.mircroservice.matching_service.application.usecases.user;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.dto.requests.DriverProfileUpdateRequest;
import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;
import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;

@Service
public class UpdateDriverProfile {
    private final DriverProfileRepository driverProfileRepository;

    public UpdateDriverProfile(DriverProfileRepository driverProfileRepository) {
        this.driverProfileRepository = driverProfileRepository;
    }

    public DriverProfileResponse execute(String userId, DriverProfileUpdateRequest request) {
        if (request == null || request.driverLicense() == null || request.driverLicense().isBlank()
            || request.licensePlate() == null || request.licensePlate().isBlank()) {
            throw new IllegalArgumentException("Thông tin bằng lái và biển số không được để trống");
        }

        DriverProfile driver = driverProfileRepository.findByUserId(userId).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy tài xế"));
        if (driver.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Hồ sơ tài xế không hoạt động");
        }

        driver.updateProfile(request.driverLicense(), request.licensePlate());
        return DriverProfileResponse.toResponse(driverProfileRepository.save(driver));
    }
}
