package com.hotel.room_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private String roomType;
    private int capacity;
    private double pricePerNight;
    private boolean available;
}