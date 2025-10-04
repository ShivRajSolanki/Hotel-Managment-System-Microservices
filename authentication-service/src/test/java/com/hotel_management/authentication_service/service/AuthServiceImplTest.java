package com.hotel_management.authentication_service.service;

import com.hotel_management.authentication_service.dto.*;
import com.hotel_management.authentication_service.entity.Staff;
import com.hotel_management.authentication_service.enums.Role;
import com.hotel_management.authentication_service.repository.StaffRepository;
import com.hotel_management.authentication_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private StaffRepository staffRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthServiceImpl authService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_success() {
        AuthRequest request = new AuthRequest("admin", "password");
        Staff user = new Staff(1L, "Admin", "admin", "hashed", Role.OWNER);

        when(staffRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("admin", "OWNER")).thenReturn("token");

        AuthResponse response = authService.login(request);

        assertEquals("token", response.getToken());
    }

    @Test
    void login_userNotFound_shouldThrow() {
        AuthRequest request = new AuthRequest("unknown", "pass");
        when(staffRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void ownerExists_true() {
        List<Staff> list = List.of(new Staff(1L, "A", "a", "x", Role.OWNER));
        when(staffRepository.findAll()).thenReturn(list);

        assertTrue(authService.ownerExists());
    }

    @Test
    void getAllStaff_byRole() {
        Staff staff = new Staff(1L, "B", "b", "p", Role.RECEPTIONIST);
        when(staffRepository.findByRole(Role.RECEPTIONIST)).thenReturn(List.of(staff));

        List<StaffDto> result = authService.getAllStaff(Role.RECEPTIONIST);

        assertEquals(1, result.size());
        assertEquals("b", result.get(0).getUsername());
    }

    @Test
    void deleteStaff_success() {
        Staff staff = new Staff(1L, "B", "b", "p", Role.MANAGER);
        when(staffRepository.findByUsername("b")).thenReturn(Optional.of(staff));

        authService.deleteStaff("b");

        verify(staffRepository).delete(staff);
    }

    @Test
    void updateStaff_success() {
        Staff staff = new Staff(1L, "C", "c", "p", Role.RECEPTIONIST);
        StaffDto dto = new StaffDto("c", "C", Role.MANAGER);

        when(staffRepository.findByUsername("c")).thenReturn(Optional.of(staff));

        StaffDto updated = authService.updateStaff("c", dto);

        assertEquals(Role.MANAGER, updated.getRole());
    }

    @Test
    void signup_success() {
        SignupRequest request = new SignupRequest("D", "d", "pw", Role.MANAGER);
        when(passwordEncoder.encode("pw")).thenReturn("hashed");

        String result = authService.signup(request);

        assertEquals("User registered successfully!", result);
    }
}