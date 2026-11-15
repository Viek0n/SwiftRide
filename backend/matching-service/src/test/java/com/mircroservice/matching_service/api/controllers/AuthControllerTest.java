package com.mircroservice.matching_service.api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mircroservice.matching_service.application.usecases.LoginDriverUseCase;
import com.mircroservice.matching_service.application.usecases.LoginUserUseCase;
import com.mircroservice.matching_service.application.usecases.RegisterDriverUseCase;
import com.mircroservice.matching_service.application.usecases.RegisterUserUseCase;
import com.mircroservice.matching_service.domain.dto.requests.DriverRegisterRequest;
import com.mircroservice.matching_service.domain.dto.requests.LoginRequest;
import com.mircroservice.matching_service.domain.dto.requests.UserRegisterRequest;
import com.mircroservice.matching_service.domain.dto.responses.DriverProfileResponse;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

class AuthControllerTest {
    private final RegisterUserUseCase registerUser = mock(RegisterUserUseCase.class);
    private final RegisterDriverUseCase registerDriver = mock(RegisterDriverUseCase.class);
    private final LoginUserUseCase loginUser = mock(LoginUserUseCase.class);
    private final LoginDriverUseCase loginDriver = mock(LoginDriverUseCase.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(
            registerUser,
            registerDriver,
            loginUser,
            loginDriver
        )).build();
    }

    @Test
    void registersUserThroughAuthRoute() throws Exception {
        when(registerUser.execute(any(UserRegisterRequest.class))).thenReturn(
            new UserResponse("user-1", "Rider", "0900000000", AccountStatus.ACTIVE, null, null)
        );

        mockMvc.perform(post("/auth/register/user")
                .contentType("application/json")
                .content("""
                    {"name":"Rider","phone":"0900000000","password":"secret"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("user-1"))
            .andExpect(jsonPath("$.password").doesNotExist());

        verify(registerUser).execute(any(UserRegisterRequest.class));
    }

    @Test
    void registersRiderProfileForExistingUser() throws Exception {
        when(registerDriver.execute(any(DriverRegisterRequest.class))).thenReturn(
            new DriverProfileResponse(
                "user-1",
                "license-1",
                "plate-1",
                AccountStatus.ACTIVE,
                null,
                null
            )
        );

        mockMvc.perform(post("/auth/register/rider")
                .contentType("application/json")
                .content("""
                    {"userId":"user-1","driverLicense":"license-1","licensePlate":"plate-1"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value("user-1"));

        verify(registerDriver).execute(any(DriverRegisterRequest.class));
    }

    @Test
    void logsInUserAndRiderThroughRoleSpecificRoutes() throws Exception {
        when(loginUser.execute(any(LoginRequest.class))).thenReturn(
            new UserResponse("user-1", "Rider", "0900000000", AccountStatus.ACTIVE, null, null)
        );
        when(loginDriver.execute(any(LoginRequest.class))).thenReturn(
            new DriverProfileResponse(
                "user-1",
                "license-1",
                "plate-1",
                AccountStatus.ACTIVE,
                null,
                null
            )
        );

        String request = """
            {"phone":"0900000000","password":"secret"}
            """;

        mockMvc.perform(post("/auth/login/user")
                .contentType("application/json")
                .content(request))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("user-1"));

        mockMvc.perform(post("/auth/login/rider")
                .contentType("application/json")
                .content(request))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("user-1"));

        verify(loginUser).execute(any(LoginRequest.class));
        verify(loginDriver).execute(any(LoginRequest.class));
    }
}
