package com.mircroservice.trip_service.application.dto;

import jakarta.validation.constraints.NotBlank;

public record DriverAssignmentRequest(@NotBlank String driverId) {}
