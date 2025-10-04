package com.hotel_management.authentication_service.service;

import com.hotel_management.authentication_service.dto.*;
import com.hotel_management.authentication_service.enums.Role;

import java.util.List;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    String signup(SignupRequest request);
    void logout(String token);
    boolean ownerExists();

    StaffDto updateStaff(String username, StaffUpdateDto staffDto);

    List<StaffDto> getAllStaff(Role role);

    void deleteStaff(String username);
}
