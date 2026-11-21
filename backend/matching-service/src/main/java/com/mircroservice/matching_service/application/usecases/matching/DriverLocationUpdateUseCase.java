package com.mircroservice.matching_service.application.usecases.matching;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.repositories.ActiveDriverRepository;
import com.mircroservice.matching_service.domain.value_objects.Position;

public class DriverLocationUpdateUseCase {
    private final ActiveDriverRepository activeDriverRepo;

    public DriverLocationUpdateUseCase(
        ActiveDriverRepository activeDriverRepo
    ){
        this.activeDriverRepo = activeDriverRepo;
    }

    public ActiveDriver execute(String userId, Position pos){
        ActiveDriver activeDriver = activeDriverRepo.findById(userId).orElseGet(
            () -> new ActiveDriver(
                userId, 
                pos
            ));
        
        activeDriver.setActivePosition(pos);
        return activeDriverRepo.save(activeDriver);
    }
}
