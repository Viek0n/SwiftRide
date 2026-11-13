package com.mircroservice.matching_service.application.services;

import java.time.Instant;

import com.mircroservice.matching_service.application.abstractions.PasswordHasher;
import com.mircroservice.matching_service.domain.dto.requests.UserRegisterRequest;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

public class UserService {
    private UserRepository riderRepository;
    private PasswordHasher passwordHasher;

    public UserService(
        UserRepository riderRepository, 
        PasswordHasher passwordHasher
    ){
        this.riderRepository = riderRepository;
        this.passwordHasher = passwordHasher;
    }

    private UserResponse toResponse(User rider) {
        return new UserResponse(
            rider.getId(),
            rider.getName(),
            rider.getPhone(),
            rider.getAccountStatus(),
            rider.getCreatedAt(),
            rider.getUpdatedAt()
        );
    }

    public UserResponse register(UserRegisterRequest request) {
        if (riderRepository.existByPhone(request.phone())) {
            throw new IllegalArgumentException("Số điện thoại đã đăng kí");
        }

        Instant now = Instant.now();

        User rider = new User(
            null,
            request.name(),
            request.phone(),
            passwordHasher.hash(request.password()),
            AccountStatus.ACTIVE,
            now,
            now
        );

        User savedRider = riderRepository.save(rider);

        return toResponse(savedRider);
    }

    public UserResponse findById(String id){
        User rider = riderRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy người dùng"));
        return toResponse(rider);
    }
}
