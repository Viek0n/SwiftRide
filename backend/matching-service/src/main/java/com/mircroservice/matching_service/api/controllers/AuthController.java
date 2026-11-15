package com.mircroservice.matching_service.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.api.routes.AuthRoute;
import com.mircroservice.matching_service.application.usecases.LoginDriverUseCase;
import com.mircroservice.matching_service.application.usecases.LoginUserUseCase;
import com.mircroservice.matching_service.application.usecases.RegisterDriverUseCase;
import com.mircroservice.matching_service.application.usecases.RegisterUserUseCase;
import com.mircroservice.matching_service.domain.dto.requests.DriverRegisterRequest;
import com.mircroservice.matching_service.domain.dto.requests.LoginRequest;
import com.mircroservice.matching_service.domain.dto.requests.UserRegisterRequest;
import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;

@RestController
@RequestMapping(AuthRoute.BASE)
public class AuthController {
    private final RegisterUserUseCase registerUser;
    private final RegisterDriverUseCase registerDriver;
    private final LoginUserUseCase loginUser;
    private final LoginDriverUseCase loginDriver;

    public AuthController(
        RegisterUserUseCase registerUser,
        RegisterDriverUseCase registerDriver,
        LoginUserUseCase loginUser,
        LoginDriverUseCase loginDriver
    ) {
        this.registerUser = registerUser;
        this.registerDriver = registerDriver;
        this.loginUser = loginUser;
        this.loginDriver = loginDriver;
    }

    @PostMapping(AuthRoute.REGISTER_USER)
    public ResponseEntity<UserResponse> registerUser(
        @RequestBody UserRegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerUser.execute(request));
    }

    @PostMapping(AuthRoute.REGISTER_RIDER)
    public ResponseEntity<DriverProfileResponse> registerRider(
        @RequestBody DriverRegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerDriver.execute(request));
    }

    @PostMapping(AuthRoute.LOGIN_USER)
    public ResponseEntity<UserResponse> loginUser(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUser.execute(request));
    }

    @PostMapping(AuthRoute.LOGIN_RIDER)
    public ResponseEntity<DriverProfileResponse> loginRider(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginDriver.execute(request));
    }
}
