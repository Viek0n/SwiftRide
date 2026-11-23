package com.mircroservice.matching_service.application.usecases.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.dto.responses.UserResponse;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@Service
public class GetUsersListUseCase {
    private final UserRepository userRepository;

    public GetUsersListUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> execute() {
        return userRepository.findAll().stream()
            .map(UserResponse::toResponse)
            .toList();
    }
}
