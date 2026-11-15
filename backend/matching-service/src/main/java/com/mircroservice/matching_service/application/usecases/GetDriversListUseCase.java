package com.mircroservice.matching_service.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;
import com.mircroservice.matching_service.domain.repositories.DriverProfileRepository;

@Service
public class GetDriversListUseCase {
    private final DriverProfileRepository driverProfileRepository;

    public GetDriversListUseCase(DriverProfileRepository driverProfileRepository) {
        this.driverProfileRepository = driverProfileRepository;
    }

    public List<DriverProfileResponse> execute() {
        return driverProfileRepository.findAll().stream()
            .map(DriverProfileResponse::toResponse)
            .toList();
    }
}
