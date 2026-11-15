package com.mircroservice.matching_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.value_objects.Position;

public interface ActiveDriverRepository {
    ActiveDriver save(ActiveDriver activeDriver);

    Optional<ActiveDriver> findById(String id);

    List<ActiveDriver> findByActiveStatus(ActiveStatus activeStatus);

    boolean transitionStatus(String userId, ActiveStatus currentStatus, ActiveStatus nextStatus);

    boolean updatePositionIfStatus(
        String userId,
        ActiveStatus currentStatus,
        Position position
    );

    boolean existsById(String id);

    void deleteById(String id);
}
