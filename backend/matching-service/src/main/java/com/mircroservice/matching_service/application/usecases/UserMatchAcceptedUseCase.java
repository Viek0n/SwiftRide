package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;

@Service
public class UserMatchAcceptedUseCase {
    private final DriverAcceptedUseCase driverAcceptedUseCase;

    public UserMatchAcceptedUseCase(DriverAcceptedUseCase driverAcceptedUseCase) {
        this.driverAcceptedUseCase = driverAcceptedUseCase;
    }

    public ActiveDriver execute(String driverUserId) {
        return driverAcceptedUseCase.execute(driverUserId);
    }
}
