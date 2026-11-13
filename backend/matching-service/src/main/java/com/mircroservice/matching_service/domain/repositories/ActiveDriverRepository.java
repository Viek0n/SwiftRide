package com.mircroservice.matching_service.domain.repositories;

import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.ActiveDriver;

public interface ActiveDriverRepository {
    ActiveDriver save(ActiveDriver activeDriver);

    Optional<ActiveDriver> findById(String id);

    Boolean existById(String Id);

    void deleteById(String id);
}
