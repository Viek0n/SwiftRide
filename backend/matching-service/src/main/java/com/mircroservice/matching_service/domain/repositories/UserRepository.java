package com.mircroservice.matching_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.mircroservice.matching_service.domain.entities.User;

public interface UserRepository{
    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByPhone(String phone);

    List<User> findAll();

    boolean existsByPhone(String phone);

    void softDeleteById(String id);

    void deleteById(String id);
}
