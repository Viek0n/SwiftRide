package com.mircroservice.matching_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.DriverProfile;

public interface DriverProfileRepository {
    DriverProfile save(DriverProfile driver);

    Optional<DriverProfile> findById(String id);

    List<DriverProfile> findAll();

    boolean existsById(String id);

    void softDeleteById(String id);

    void deleteById(String id);
}
