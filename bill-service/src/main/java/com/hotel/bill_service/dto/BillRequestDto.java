package com.hotel.bill_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BillRequestDto {
    @NotBlank(message = "Reservation code is required")
    private String reservationCode;
}