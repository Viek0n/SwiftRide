package com.mircroservice.matching_service.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mircroservice.matching_service.api.routes.ActiveDriverRoute;
import com.mircroservice.matching_service.application.usecases.matching.DriverLocationUpdateUseCase;
import com.mircroservice.matching_service.application.usecases.matching.DriverStartMatchingUseCase;
import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.value_objects.Position;

import lombok.AllArgsConstructor;

@RestController  
@AllArgsConstructor 
@RequestMapping(ActiveDriverRoute.BASE)
public class ActiveDriverController {
    private final DriverLocationUpdateUseCase locationUpdate;
    private final DriverStartMatchingUseCase startMatching;

    @PutMapping(ActiveDriverRoute.LOCATION)
    public ResponseEntity<ActiveDriver> updateLocation(
        @PathVariable String driverId,
        @RequestBody Position position
    ) {
        return ResponseEntity.ok(locationUpdate.execute(driverId, position));
    }

    @PutMapping(ActiveDriverRoute.START_MATCHING)
    public ResponseEntity<Boolean> startMatching(@PathVariable String driverId) {
        return ResponseEntity.ok(startMatching.execute(driverId));
    }

}
