package com.mircroservice.matching_service.api.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.api.routes.AuthRoute;
import com.mircroservice.matching_service.application.usecases.user.LoginDriverUseCase;
import com.mircroservice.matching_service.application.usecases.user.LoginUserUseCase;
import com.mircroservice.matching_service.application.usecases.user.RegisterDriverUseCase;
import com.mircroservice.matching_service.application.usecases.user.RegisterUserUseCase;
import com.mircroservice.matching_service.domain.dto.requests.DriverRegisterRequest;
import com.mircroservice.matching_service.domain.dto.requests.LoginRequest;
import com.mircroservice.matching_service.domain.dto.requests.UserRegisterRequest;
import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;
import com.mircroservice.matching_service.domain.dto.responses.DriverLoginResponse;
import com.mircroservice.matching_service.domain.dto.responses.UserLoginResponse;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(AuthRoute.BASE)
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@AllArgsConstructor 
public class AuthController {
    private final RegisterUserUseCase registerUser;
    private final RegisterDriverUseCase registerDriver;
    private final LoginUserUseCase loginUser;
    private final LoginDriverUseCase loginDriver;

    @PostMapping(AuthRoute.REGISTER_USER)
    public ResponseEntity<UserResponse> registerUser(
        @RequestBody UserRegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerUser.execute(request));
    }

    @PostMapping(AuthRoute.REGISTER_DRIVER)
    public ResponseEntity<DriverProfileResponse> registerDriver(
        @RequestBody DriverRegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerDriver.execute(request));
    }

    @PostMapping(AuthRoute.LOGIN_USER)
    public ResponseEntity<UserLoginResponse> loginUser(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUser.execute(request));
    }

    @PostMapping({AuthRoute.LOGIN_DRIVER, AuthRoute.LOGIN_RIDER})
    public ResponseEntity<DriverLoginResponse> loginDriver(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginDriver.execute(request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidAuthRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
}
