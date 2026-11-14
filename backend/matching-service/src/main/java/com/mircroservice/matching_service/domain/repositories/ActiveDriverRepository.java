package com.mircroservice.matching_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;

public interface ActiveDriverRepository {
    ActiveDriver save(ActiveDriver activeDriver);

    Optional<ActiveDriver> findById(String id);

    List<ActiveDriver> findByActiveStatus(ActiveStatus activeStatus);

    boolean existsById(String id);

    void deleteById(String id);
}
