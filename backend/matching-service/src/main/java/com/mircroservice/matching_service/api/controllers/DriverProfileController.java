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

import com.mircroservice.matching_service.api.routes.DriverProfileRoute;
import com.mircroservice.matching_service.application.usecases.user.DeleteDriverProfile;
import com.mircroservice.matching_service.application.usecases.user.FindDriverByIdUseCase;
import com.mircroservice.matching_service.application.usecases.user.GetDriversListUseCase;
import com.mircroservice.matching_service.application.usecases.user.SoftDeleteDriverProfileUsecase;
import com.mircroservice.matching_service.application.usecases.user.UpdateDriverProfile;
import com.mircroservice.matching_service.domain.dto.requests.DriverProfileUpdateRequest;
import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor 
@RestController 
@RequestMapping(DriverProfileRoute.BASE)
public class DriverProfileController {
    private final GetDriversListUseCase getDriversList;
    private final FindDriverByIdUseCase findDriverById;
    private final UpdateDriverProfile updateDriverProfile;
    private final DeleteDriverProfile deleteDriverProfile;
    private final SoftDeleteDriverProfileUsecase softDeleteDriverProfile;

    @GetMapping(DriverProfileRoute.LIST)
    public ResponseEntity<List<DriverProfileResponse>> list() {
        return ResponseEntity.ok(getDriversList.execute());
    }

    @GetMapping(DriverProfileRoute.PROFILE)
    public ResponseEntity<DriverProfileResponse> findById(@PathVariable String userId) {
        return ResponseEntity.ok(findDriverById.execute(userId));
    }

    @PutMapping(DriverProfileRoute.UPDATE)
    public ResponseEntity<DriverProfileResponse> update(
        @PathVariable String userId,
        @RequestBody DriverProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(updateDriverProfile.execute(userId, request));
    }

    @DeleteMapping(DriverProfileRoute.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String userId) {
        deleteDriverProfile.execute(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(DriverProfileRoute.SOFT_DELETE)
    public ResponseEntity<Void> softDelete(@PathVariable String userId) {
        softDeleteDriverProfile.execute(userId);
        return ResponseEntity.noContent().build();
    }
}
