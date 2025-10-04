package com.hotel_management.authentication_service.controller;

import com.hotel_management.authentication_service.dto.*;
import com.hotel_management.authentication_service.enums.Role;
import com.hotel_management.authentication_service.service.AuthService;
import com.hotel_management.authentication_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/init-owner")
    public ResponseEntity<String> initOwner(@Valid @RequestBody SignupRequest request) {
        log.info("initOwner request received for username: {}", request.getUsername());

        if (request.getRole() != Role.OWNER) {
            log.warn("Invalid role provided in initOwner: {}", request.getRole());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This route only creates OWNER role.");
        }

        if (authService.ownerExists()) {
            log.warn("Attempt to create additional OWNER when one already exists.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("An OWNER already exists.");
        }

        String result = authService.signup(request);
        log.info("OWNER created successfully: {}", request.getUsername());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request, @RequestHeader("Authorization") String authHeader) {
        String role = extractRoleFromToken(authHeader);
        log.info("Signup request received. Creator role: {}, New user role: {}", role, request.getRole());

        if (request.getRole() == Role.OWNER && !role.equals("OWNER")) {
            log.warn("Unauthorized attempt to create OWNER by {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only OWNER can create other OWNERS");
        }

        if (request.getRole() == Role.MANAGER && !role.equals("OWNER")) {
            log.warn("Unauthorized attempt to create MANAGER by {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only OWNER can create MANAGER");
        }

        if (request.getRole() == Role.RECEPTIONIST && !(role.equals("OWNER") || role.equals("MANAGER"))) {
            log.warn("Unauthorized attempt to create RECEPTIONIST by {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only OWNER or MANAGER can create RECEPTIONIST");
        }

        String result = authService.signup(request);
        log.info("User {} with role {} registered successfully by {}", request.getUsername(), request.getRole(), role);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/owner-exists")
    public ResponseEntity<?> ownerExists() {
        boolean exists = authService.ownerExists();
        log.info("Checking if OWNER exists: {}", exists);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        log.info("Logout request received.");
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully.");
    }

    @GetMapping("/all/{role}")
    public ResponseEntity<?> getAllStaff(@PathVariable Role role, @RequestHeader("Authorization") String authHeader) {
        String userRole = extractRoleFromToken(authHeader);
        log.info("getAllStaff called by: {}, target role: {}", userRole, role);

        if (!userRole.equals("OWNER")) {
            log.warn("Access denied for role {} attempting to list staff", userRole);
            return ResponseEntity.status(403).body("Access Denied: Only OWNER can retrieve staff details.");
        }

        return ResponseEntity.ok(authService.getAllStaff(role));
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<?> updateStaff(@PathVariable String username, @Valid @RequestBody StaffUpdateDto staffDto, @RequestHeader("Authorization") String authHeader) {
        String role = extractRoleFromToken(authHeader);
        log.info("updateStaff called by {}, target username: {}", role, username);

        if (!role.equals("OWNER")) {
            log.warn("Unauthorized update attempt by {}", role);
            return ResponseEntity.status(403).body("Access Denied: Only OWNER can update staff details.");
        }

        return ResponseEntity.ok(authService.updateStaff(username, staffDto));
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> deleteStaff(@PathVariable String username, @RequestHeader("Authorization") String authHeader) {
        String role = extractRoleFromToken(authHeader);
        log.info("deleteStaff called by {}, target username: {}", role, username);

        if (!role.equals("OWNER")) {
            log.warn("Unauthorized delete attempt by {}", role);
            return ResponseEntity.status(403).body("Only OWNER can delete staff.");
        }

        authService.deleteStaff(username);
        log.info("Staff {} deleted by {}", username, role);
        return ResponseEntity.ok("Staff deleted successfully.");
    }

    private String extractRoleFromToken(String authHeader) {
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extractAllClaims(token);
        return claims.get("role", String.class);
    }
}