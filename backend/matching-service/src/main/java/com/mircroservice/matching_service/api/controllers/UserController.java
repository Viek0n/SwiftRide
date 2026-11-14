package com.mircroservice.matching_service.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.application.usecases.FindUserByIdUseCase;
import com.mircroservice.matching_service.application.usecases.RegisterUserUseCase;
import com.mircroservice.matching_service.domain.dto.requests.UserRegisterRequest;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final RegisterUserUseCase registerUser;
    private final FindUserByIdUseCase findUserById;

    public UserController(RegisterUserUseCase registerUser, FindUserByIdUseCase findUserById) {
        this.registerUser = registerUser;
        this.findUserById = findUserById;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
        @RequestBody UserRegisterRequest request
    ) {
        UserResponse response = registerUser.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(findUserById.execute(id));
    }
}
