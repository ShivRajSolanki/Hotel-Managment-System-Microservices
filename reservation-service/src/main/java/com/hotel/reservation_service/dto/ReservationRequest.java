package com.hotel.reservation_service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationRequest {
    @Min(value = 0, message = "Number of children cannot be negative")
    private int numberOfChildren;

    @Min(value = 1, message = "At least one adult is required")
    private int numberOfAdults;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or later")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;

    @NotNull(message = "Guest ID is required")
    private Long guestId;

    @NotNull(message = "Room ID is required")
    private Long roomId;
}
