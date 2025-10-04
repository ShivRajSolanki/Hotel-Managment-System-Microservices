package com.hotel.room_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    private String roomType;

    @Min(value = 1, message = "Room capacity must be at least 1")
    private int capacity;

    @Positive(message = "Price per night must be greater than 0")
    private double pricePerNight;

    private boolean available;

}
