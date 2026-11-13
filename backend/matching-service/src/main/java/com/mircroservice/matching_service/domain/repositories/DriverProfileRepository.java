package com.mircroservice.matching_service.domain.repositories;

import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.DriverProfile;

public interface DriverProfileRepository {
    DriverProfile save(DriverProfile rider);

    Optional<DriverProfile> findById(String id);

    Boolean existById(String Id);

    void softDeleteById(String id);

    void deleteById(String id);
}
