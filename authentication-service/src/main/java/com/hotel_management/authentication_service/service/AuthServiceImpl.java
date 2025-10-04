package com.hotel_management.authentication_service.service;

import com.hotel_management.authentication_service.dto.*;
import com.hotel_management.authentication_service.entity.Staff;
import com.hotel_management.authentication_service.enums.Role;
import com.hotel_management.authentication_service.repository.StaffRepository;
import com.hotel_management.authentication_service.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Attempting login for user: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Staff user = staffRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("Login failed - user not found: {}", request.getUsername());
                    return new UsernameNotFoundException("User not found");
                });

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        log.info("Login successful for user: {}", user.getUsername());

        return new AuthResponse(token);
    }

    @Override
    public boolean ownerExists() {
        boolean exists = staffRepository.findAll().stream()
                .anyMatch(user -> user.getRole() == Role.OWNER);

        log.info("Owner exists check: {}", exists);
        return exists;
    }

    @Override
    public List<StaffDto> getAllStaff(Role role) {
        log.info("Fetching staff with role: {}", role.name());

        return staffRepository.findByRole(role).stream()
                .map(staff -> StaffDto.builder()
                        .username(staff.getUsername())
                        .name(staff.getName())
                        .role(staff.getRole())
                        .build())
                .toList();
    }

    @Override
    public void deleteStaff(String username) {
        log.info("Attempting to delete staff with username: {}", username);

        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Delete failed - staff not found: {}", username);
                    return new EntityNotFoundException("Staff not found with username: " + username);
                });

        staffRepository.delete(staff);
        log.info("Staff deleted: {}", username);
    }

    @Override
    public StaffDto updateStaff(String username, StaffUpdateDto staffDto) {
        log.info("Updating staff: {}", username);

        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Update failed - staff not found: {}", username);
                    return new EntityNotFoundException("Staff Not Found with Username " + username);
                });

        staff.setRole(staffDto.getRole());
        staffRepository.save(staff);

        log.info("Staff updated: {} with role {}", staff.getUsername(), staff.getRole());
        return StaffDto.builder()
                .username(staff.getUsername())
                .name(staff.getName())
                .role(staff.getRole())
                .build();
    }

    @Override
    public String signup(SignupRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        Staff user = Staff.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.RECEPTIONIST)
                .build();

        staffRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        return "User registered successfully!";
    }

    @Override
    public void logout(String token) {
        log.info("Logout requested for token: {}", token);
    }
}