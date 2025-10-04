package com.hotel.reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponse {
    private Long id;
    private String code;
    private int numberOfChildren;
    private int numberOfAdults;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private int numberOfNights;
    private Long guestId;
    private Long roomId;
    private String guestName;
    private String guestEmail;
    private String roomNumber;
    private String roomType;
    private double rate;

}

