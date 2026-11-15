package com.mircroservice.matching_service.api.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mircroservice.matching_service.application.usecases.DriverMatchingUseCase;
import com.mircroservice.matching_service.domain.entities.ActiveDriver;
import com.mircroservice.matching_service.domain.enumerators.ActiveStatus;
import com.mircroservice.matching_service.domain.value_objects.Position;

class ActiveDriverControllerTest {
    private final DriverMatchingUseCase driverMatching = mock(DriverMatchingUseCase.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
            new ActiveDriverController(driverMatching)
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
}
