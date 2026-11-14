package com.mircroservice.matching_service.application.usecases;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.dto.responses.UserResponse;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@Service
public class FindUserByIdUseCase {
    private final UserRepository userRepository;

    public FindUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(String id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy người dùng"));
        return UserResponse.toResponse(user);
    }
}
