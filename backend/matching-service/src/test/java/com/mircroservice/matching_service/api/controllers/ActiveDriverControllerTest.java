package com.mircroservice.matching_service.api.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mircroservice.matching_service.application.usecases.DriverMatchAcceptedUseCase;
import com.mircroservice.matching_service.application.usecases.DriverMatchRejectUsecase;
import com.mircroservice.matching_service.application.usecases.DriverMatchingUseCase;
import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.value_objects.Position;

class ActiveDriverControllerTest {
    private final DriverMatchingUseCase driverMatching = mock(DriverMatchingUseCase.class);
    private final DriverMatchAcceptedUseCase acceptMatch = mock(DriverMatchAcceptedUseCase.class);
    private final DriverMatchRejectUsecase rejectMatch = mock(DriverMatchRejectUsecase.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
            new ActiveDriverController(driverMatching, acceptMatch, rejectMatch)
        ).build();
    }

    @Test
    void locationUpdatePutsDriverOnlineUsingMapLibreCoordinates() throws Exception {
        when(driverMatching.execute("driver-1", new Position(10.0, 106.0))).thenReturn(
            new ActiveDriver("driver-1", ActiveStatus.ONLINE, new Position(10.0, 106.0))
        );

        mockMvc.perform(put("/active-drivers/driver-1/location")
                .contentType("application/json")
                .content("""
                    {"lat":10.0,"lng":106.0}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activeStatus").value("ONLINE"))
            .andExpect(jsonPath("$.activePosition.latitude").value(10.0));
    }

    @Test
    void acceptsDriverMatch() throws Exception {
        when(acceptMatch.execute("driver-1")).thenReturn(
            new ActiveDriver("driver-1", ActiveStatus.IN_MATCH, new Position(10.0, 106.0))
        );

        mockMvc.perform(post("/active-drivers/driver-1/accept"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activeStatus").value("IN_MATCH"));
    }

    @Test
    void rejectsDriverMatch() throws Exception {
        when(rejectMatch.execute("driver-1")).thenReturn(
            new ActiveDriver("driver-1", ActiveStatus.ONLINE, new Position(10.0, 106.0))
        );

        mockMvc.perform(post("/active-drivers/driver-1/reject"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activeStatus").value("ONLINE"));
    }
}
