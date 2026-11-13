package com.mircroservice.matching_service.domain.repositories;

import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.User;

public interface UserRepository{
    User save(User rider);

    Optional<User> findById(String id);

    Boolean existByPhone(String phone);

    void softDeleteById(String id);

    void deleteById(String id);
}
