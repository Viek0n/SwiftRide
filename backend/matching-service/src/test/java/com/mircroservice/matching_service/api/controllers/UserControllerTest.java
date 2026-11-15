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

import com.mircroservice.matching_service.application.usecases.DeleteUserUseCase;
import com.mircroservice.matching_service.application.usecases.FindUserByIdUseCase;
import com.mircroservice.matching_service.application.usecases.GetUsersListUseCase;
import com.mircroservice.matching_service.application.usecases.SoftDeleteUserUseCase;
import com.mircroservice.matching_service.application.usecases.UpdateUserUsecase;
import com.mircroservice.matching_service.domain.dto.responses.UserResponse;
import com.mircroservice.matching_service.domain.enumerators.AccountStatus;

class UserControllerTest {
    private final FindUserByIdUseCase findUser = mock(FindUserByIdUseCase.class);
    private final GetUsersListUseCase getUsers = mock(GetUsersListUseCase.class);
    private final UpdateUserUsecase updateUser = mock(UpdateUserUsecase.class);
    private final DeleteUserUseCase deleteUser = mock(DeleteUserUseCase.class);
    private final SoftDeleteUserUseCase softDeleteUser = mock(SoftDeleteUserUseCase.class);
    private final UserResponse user = new UserResponse(
        "user-1",
        "Rider",
        "0900000000",
        AccountStatus.ACTIVE,
        null,
        null
    );
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(
            findUser,
            getUsers,
            updateUser,
            deleteUser,
            softDeleteUser
        )).build();
    }

    @Test
    void listsAndFindsUsers() throws Exception {
        when(getUsers.execute()).thenReturn(List.of(user));
        when(findUser.execute("user-1")).thenReturn(user);

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("user-1"));

        mockMvc.perform(get("/users/user-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Rider"));
    }

    @Test
    void updatesUserProfile() throws Exception {
        when(updateUser.execute(org.mockito.ArgumentMatchers.eq("user-1"), any()))
            .thenReturn(user);

        mockMvc.perform(put("/users/user-1")
                .contentType("application/json")
                .content("""
                    {"name":"Rider","phone":"0900000000"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("user-1"));
    }

    @Test
    void supportsHardAndSoftDelete() throws Exception {
        mockMvc.perform(delete("/users/user-1"))
            .andExpect(status().isNoContent());
        mockMvc.perform(patch("/users/user-1/soft-delete"))
            .andExpect(status().isNoContent());

        verify(deleteUser).execute("user-1");
        verify(softDeleteUser).execute("user-1");
    }
}
