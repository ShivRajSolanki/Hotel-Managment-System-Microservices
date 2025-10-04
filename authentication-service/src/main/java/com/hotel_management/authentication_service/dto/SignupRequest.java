package com.hotel_management.authentication_service.dto;

import com.hotel_management.authentication_service.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 6)
    private String password;
    @NotNull
    private Role role;
}

