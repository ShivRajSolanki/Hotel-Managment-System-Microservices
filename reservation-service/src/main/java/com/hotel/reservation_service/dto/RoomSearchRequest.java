package com.hotel.reservation_service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RoomSearchRequest {
    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or later")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "At least one guest is required")
    private int numberOfGuests;

    @NotBlank
    private String roomType;
}
