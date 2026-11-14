package com.mircroservice.matching_service.application.usecases;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.dto.requests.DriverRegisterRequest;
import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;
import com.mircroservice.matching_service.domain.entities.DriverProfile;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@Service
public class RegisterDriverUseCase {
    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;

    public RegisterDriverUseCase(
        UserRepository userRepository,
        DriverProfileRepository driverProfileRepository
    ) {
        this.userRepository = userRepository;
        this.driverProfileRepository = driverProfileRepository;
    }

    public DriverProfileResponse execute(DriverRegisterRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy người dùng"));
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Tài khoản không thể đăng ký làm tài xế");
        }
        if (driverProfileRepository.existsById(request.userId())) {
            throw new IllegalArgumentException("Hồ sơ tài xế đã tồn tại");
        }

        Instant now = Instant.now();
        DriverProfile driver = new DriverProfile(
            user.getId(),
            request.driverLicense(),
            request.licensePlate(),
            AccountStatus.ACTIVE,
            now,
            now
        );
        return DriverProfileResponse.toResponse(driverProfileRepository.save(driver));
    }
}
