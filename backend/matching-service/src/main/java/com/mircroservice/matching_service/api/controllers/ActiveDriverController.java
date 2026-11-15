package com.mircroservice.matching_service.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.api.routes.ActiveDriverRoute;
import com.mircroservice.matching_service.application.usecases.DriverMatchAcceptedUseCase;
import com.mircroservice.matching_service.application.usecases.DriverMatchRejectUsecase;
import com.mircroservice.matching_service.application.usecases.DriverMatchingUseCase;
import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.value_objects.Position;

@RestController  
@RequestMapping(ActiveDriverRoute.ACTIVE_DRIVER_CONTROLLER)
public class ActiveDriverController {
    private final DriverMatchingUseCase driverMatching;
    private final DriverMatchAcceptedUseCase acceptMatch;
    private final DriverMatchRejectUsecase rejectMatch;

    public ActiveDriverController(
        DriverMatchingUseCase driverMatching,
        DriverMatchAcceptedUseCase acceptMatch,
        DriverMatchRejectUsecase rejectMatch
    ) {
        this.driverMatching = driverMatching;
        this.acceptMatch = acceptMatch;
        this.rejectMatch = rejectMatch;
    }

    @PutMapping(ActiveDriverRoute.LOCATION)
    public ResponseEntity<ActiveDriver> updateLocation(
        @PathVariable String driverId,
        @RequestBody Position position
    ) {
        return ResponseEntity.ok(driverMatching.execute(driverId, position));
    }

    @PostMapping(ActiveDriverRoute.ACCEPT)
    public ResponseEntity<ActiveDriver> accept(@PathVariable String driverId) {
        return ResponseEntity.ok(acceptMatch.execute(driverId));
    }

    @PostMapping(ActiveDriverRoute.REJECT)
    public ResponseEntity<ActiveDriver> reject(@PathVariable String driverId) {
        return ResponseEntity.ok(rejectMatch.execute(driverId));
    }
}
