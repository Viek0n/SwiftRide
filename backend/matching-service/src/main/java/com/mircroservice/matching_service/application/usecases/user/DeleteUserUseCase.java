package com.mircroservice.matching_service.application.usecases.user;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@Service
public class DeleteUserUseCase {
    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final ActiveDriverRepository activeDriverRepository;

    public DeleteUserUseCase(
        UserRepository userRepository,
        DriverProfileRepository driverProfileRepository,
        ActiveDriverRepository activeDriverRepository
    ) {
        this.userRepository = userRepository;
        this.driverProfileRepository = driverProfileRepository;
        this.activeDriverRepository = activeDriverRepository;
    }

    public void execute(String userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }

        activeDriverRepository.deleteById(userId);
        if (driverProfileRepository.existsById(userId)) {
            driverProfileRepository.deleteById(userId);
        }
        userRepository.deleteById(userId);
    }
}
