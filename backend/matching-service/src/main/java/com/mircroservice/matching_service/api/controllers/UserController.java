package com.mircroservice.matching_service.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.api.routes.UserRoute;
import com.mircroservice.matching_service.application.usecases.DeleteUserUseCase;
import com.mircroservice.matching_service.application.usecases.FindUserByIdUseCase;
import com.mircroservice.matching_service.application.usecases.GetUsersListUseCase;
import com.mircroservice.matching_service.application.usecases.SoftDeleteUserUseCase;
import com.mircroservice.matching_service.application.usecases.UpdateUserUsecase;
import com.mircroservice.matching_service.domain.dto.requests.UserUpdateRequest;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;

@RestController
@RequestMapping(UserRoute.BASE)
public class UserController {
    private final FindUserByIdUseCase findUserById;
    private final GetUsersListUseCase getUsersList;
    private final UpdateUserUsecase updateUser;
    private final DeleteUserUseCase deleteUser;
    private final SoftDeleteUserUseCase softDeleteUser;

    public UserController(
        FindUserByIdUseCase findUserById,
        GetUsersListUseCase getUsersList,
        UpdateUserUsecase updateUser,
        DeleteUserUseCase deleteUser,
        SoftDeleteUserUseCase softDeleteUser
    ) {
        this.findUserById = findUserById;
        this.getUsersList = getUsersList;
        this.updateUser = updateUser;
        this.deleteUser = deleteUser;
        this.softDeleteUser = softDeleteUser;
    }

    @GetMapping(UserRoute.LIST)
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(getUsersList.execute());
    }

    @GetMapping(UserRoute.PROFILE)
    public ResponseEntity<UserResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(findUserById.execute(id));
    }

    @PutMapping(UserRoute.UPDATE)
    public ResponseEntity<UserResponse> update(
        @PathVariable String id,
        @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(updateUser.execute(id, request));
    }

    @DeleteMapping(UserRoute.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteUser.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(UserRoute.SOFT_DELETE)
    public ResponseEntity<Void> softDelete(@PathVariable String id) {
        softDeleteUser.execute(id);
        return ResponseEntity.noContent().build();
    }
}
