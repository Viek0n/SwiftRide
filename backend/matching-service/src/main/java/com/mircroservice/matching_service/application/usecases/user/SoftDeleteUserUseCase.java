package com.mircroservice.matching_service.application.usecases.user;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@Service
public class SoftDeleteUserUseCase {
    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final ActiveDriverRepository activeDriverRepository;

    public SoftDeleteUserUseCase(
        UserRepository userRepository,
        DriverProfileRepository driverProfileRepository,
        ActiveDriverRepository activeDriverRepository
    ) {
        this.userRepository = userRepository;
        this.driverProfileRepository = driverProfileRepository;
        this.activeDriverRepository = activeDriverRepository;
    }

    public void execute(String userId) {
        var user = userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy người dùng"));
        if (user.getAccountStatus() == AccountStatus.SOFT_DELETE) {
            return;
        }

        userRepository.softDeleteById(userId);
        if (driverProfileRepository.existsByUserId(userId)) {
            driverProfileRepository.softDeleteById(userId);
        }
        activeDriverRepository.deleteById(userId);
    }
}
