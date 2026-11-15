package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;

@Service
public class UserMatchCancleUseCase {
    private final MatchRejectedUseCase matchRejectedUseCase;

    public UserMatchCancleUseCase(MatchRejectedUseCase matchRejectedUseCase) {
        this.matchRejectedUseCase = matchRejectedUseCase;
    }

    public ActiveDriver execute(String driverUserId) {
        return matchRejectedUseCase.execute(driverUserId);
    }
}
