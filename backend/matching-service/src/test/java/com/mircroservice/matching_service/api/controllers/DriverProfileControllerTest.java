package com.mircroservice.matching_service.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mircroservice.matching_service.application.usecases.DeleteDriverProfile;
import com.mircroservice.matching_service.application.usecases.FindDriverByIdUseCase;
import com.mircroservice.matching_service.application.usecases.GetDriversListUseCase;
import com.mircroservice.matching_service.application.usecases.SoftDeleteDriverProfileUsecase;
import com.mircroservice.matching_service.application.usecases.UpdateDriverProfile;
import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

class DriverProfileControllerTest {
    private final GetDriversListUseCase getDrivers = mock(GetDriversListUseCase.class);
    private final FindDriverByIdUseCase findDriver = mock(FindDriverByIdUseCase.class);
    private final UpdateDriverProfile updateDriver = mock(UpdateDriverProfile.class);
    private final DeleteDriverProfile deleteDriver = mock(DeleteDriverProfile.class);
    private final SoftDeleteDriverProfileUsecase softDeleteDriver =
        mock(SoftDeleteDriverProfileUsecase.class);
    private final DriverProfileResponse driver = new DriverProfileResponse(
        "driver-1",
        "DL-1",
        "PLATE-1",
        AccountStatus.ACTIVE,
        null,
        null
    );
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DriverProfileController(
            getDrivers,
            findDriver,
            updateDriver,
            deleteDriver,
            softDeleteDriver
        )).build();
    }

    @Test
    void listsAndFindsDriverProfiles() throws Exception {
        when(getDrivers.execute()).thenReturn(List.of(driver));
        when(findDriver.execute("driver-1")).thenReturn(driver);

        mockMvc.perform(get("/drivers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value("driver-1"));

        mockMvc.perform(get("/drivers/driver-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.driverLicense").value("DL-1"));
    }

    @Test
    void updatesDriverProfile() throws Exception {
        when(updateDriver.execute(org.mockito.ArgumentMatchers.eq("driver-1"), any()))
            .thenReturn(driver);

        mockMvc.perform(put("/drivers/driver-1")
                .contentType("application/json")
                .content("""
                    {"driverLicense":"DL-1","licensePlate":"PLATE-1"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("driver-1"));
    }

    @Test
    void supportsHardAndSoftDelete() throws Exception {
        mockMvc.perform(delete("/drivers/driver-1"))
            .andExpect(status().isNoContent());
        mockMvc.perform(patch("/drivers/driver-1/soft-delete"))
            .andExpect(status().isNoContent());

        verify(deleteDriver).execute("driver-1");
        verify(softDeleteDriver).execute("driver-1");
    }
}
