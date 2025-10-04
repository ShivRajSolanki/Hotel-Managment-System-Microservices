package com.hotel.reservation_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RoomRequest {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    private String roomType;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;

    @Positive(message = "Price per night must be positive")
    private double pricePerNight;

    private boolean available;
}
