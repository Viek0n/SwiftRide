package com.mircroservice.matching_service.domain.repositories;

import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.Driver;

public interface DriverRepository {
    Driver save(Driver driver);

    Optional<Driver> findById(String id);

    void deleteById(String id);
}
