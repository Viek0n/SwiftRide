package com.mircroservice.matching_service.domain.repositories;

import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.Rider;

public interface RiderRepository{

    Rider save(Rider rider);

    Optional<Rider> findById(String id);

    void deleteById(String id);
}
