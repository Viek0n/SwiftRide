package com.mircroservice.matching_service.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.api.routes.UserRoute;

import com.mircroservice.matching_service.application.usecases.FindUserByIdUseCase;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;

@RestController
@RequestMapping(UserRoute.BASE)
public class UserController {
    private final FindUserByIdUseCase findUserById;

    public UserController(FindUserByIdUseCase findUserById) {
        this.findUserById = findUserById;
    }

    @GetMapping(UserRoute.PROFILE)
    public ResponseEntity<UserResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(findUserById.execute(id));
    }
}
