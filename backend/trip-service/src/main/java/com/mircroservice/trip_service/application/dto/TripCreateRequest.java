package com.mircroservice.trip_service.application.dto;

import com.mircroservice.trip_service.domain.value_objects.Position;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TripCreateRequest(
    @NotBlank String riderId,
    @NotNull @Valid Position from,
    @NotNull @Valid Position to
) {}
