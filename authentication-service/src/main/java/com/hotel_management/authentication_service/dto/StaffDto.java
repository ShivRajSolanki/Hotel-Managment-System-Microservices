package com.hotel_management.authentication_service.dto;

import com.hotel_management.authentication_service.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StaffDto {
    @NotBlank
    private String name;
    @NotBlank
    private String username;
    @NotNull
    private Role role;

}
