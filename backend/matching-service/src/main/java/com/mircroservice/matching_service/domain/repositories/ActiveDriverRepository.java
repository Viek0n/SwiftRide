package com.mircroservice.matching_service.domain.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStage;
import com.mircroservice.matching_service.domain.value_objects.Position;

public interface ActiveDriverRepository {
    ActiveDriver save(ActiveDriver activeDriver);

    Optional<ActiveDriver> findById(String id);

    Optional<ActiveDriver> findByUserId(String id);

    List<ActiveDriver> findByActiveStage(ActiveStage activeStage);

    boolean updatePosition(String userId, Position position);

    boolean updateStatus(String userId, ActiveStage stage);

    boolean existsByUserId(String id);

    void deleteById(String id);

    //Get only 1 nearest driver
    Optional<ActiveDriver> findNearestAvailableDriver(
        Position riderPosition, 
        ArrayList<String> excludeDriverIds
    );
    
    //Try reserve driver 
    Boolean tryReserveDriver(
        String driverId, 
        String tripId
    );

    boolean releaseDriverIfReservedForTrip(
        String driverId,
        String tripId
    );
}
