package com.hotel_management.authentication_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel_management.authentication_service.dto.*;
import com.hotel_management.authentication_service.enums.Role;
import com.hotel_management.authentication_service.service.AuthService;
import com.hotel_management.authentication_service.service.StaffDetailsService;
import com.hotel_management.authentication_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;


    @MockitoBean private AuthService authService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private StaffDetailsService staffDetailsService;

    private String getBearer(Role role) {
        Claims claims = mock(Claims.class);
        when(claims.get("role", String.class)).thenReturn(role.name());
        when(jwtUtil.extractAllClaims(any())).thenReturn(claims);
        return "Bearer mock.jwt.token";
    }

    @Test
    void initOwner_success() throws Exception {
        SignupRequest request = new SignupRequest("Owner", "owner1", "pass", Role.OWNER);

        when(authService.ownerExists()).thenReturn(false);
        when(authService.signup(any())).thenReturn("Owner created");

        mockMvc.perform(post("/auth/init-owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Owner created"));
    }

    @Test
    void signup_receptionistByOwner_success() throws Exception {
        SignupRequest request = new SignupRequest("Rec", "rec1", "pw", Role.RECEPTIONIST);
        when(authService.signup(any())).thenReturn("Registered");

        mockMvc.perform(post("/auth/signup")
                        .header("Authorization", getBearer(Role.OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registered"));
    }

    @Test
    void login_success() throws Exception {
        AuthRequest req = new AuthRequest("manager", "pw");
        AuthResponse res = new AuthResponse("jwt");

        when(authService.login(any())).thenReturn(res);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"));
    }

    @Test
    void ownerExists_success() throws Exception {
        when(authService.ownerExists()).thenReturn(true);

        mockMvc.perform(get("/auth/owner-exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    void logout_success() throws Exception {
        doNothing().when(authService).logout(any());

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", getBearer(Role.MANAGER)))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully."));
    }

    @Test
    void getAllStaff_ownerAccess_success() throws Exception {
        when(authService.getAllStaff(Role.MANAGER)).thenReturn(List.of());

        mockMvc.perform(get("/auth/all/MANAGER")
                        .header("Authorization", getBearer(Role.OWNER)))
                .andExpect(status().isOk());
    }

    @Test
    void updateStaff_byOwner_success() throws Exception {
        StaffDto dto = new StaffDto("Receptionist", "rec", Role.RECEPTIONIST);
        when(authService.updateStaff(eq("rec"), any())).thenReturn(dto);

        mockMvc.perform(put("/auth/update/rec")
                        .header("Authorization", getBearer(Role.OWNER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("rec"));
    }

    @Test
    void deleteStaff_byOwner_success() throws Exception {
        doNothing().when(authService).deleteStaff("target");

        mockMvc.perform(delete("/auth/delete/target")
                        .header("Authorization", getBearer(Role.OWNER)))
                .andExpect(status().isOk())
                .andExpect(content().string("Staff deleted successfully."));
    }
}