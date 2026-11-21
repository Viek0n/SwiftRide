package com.mircroservice.matching_service.application.usecases.user;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.dto.requests.UserUpdateRequest;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;
import com.mircroservice.matching_service.domain.entities.User;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;
import com.mircroservice.matching_service.domain.repositories.UserRepository;

@Service
public class UpdateUserUsecase {
    private final UserRepository userRepository;

    public UpdateUserUsecase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(String userId, UserUpdateRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()
            || request.phone() == null || request.phone().isBlank()) {
            throw new IllegalArgumentException("Tên và số điện thoại không được để trống");
        }

        User user = userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("Không tìm thấy người dùng"));
        if (user.getAccountStatus() == AccountStatus.SOFT_DELETE) {
            throw new IllegalStateException("Không thể cập nhật tài khoản đã xóa");
        }
        if (!Objects.equals(user.getPhone(), request.phone())
            && userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Số điện thoại đã đăng kí");
        }

        user.updateProfile(request.name(), request.phone());
        return UserResponse.toResponse(userRepository.save(user));
    }
}
