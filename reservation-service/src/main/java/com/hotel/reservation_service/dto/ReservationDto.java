package com.hotel.reservation_service.dto;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ReservationDto {
    private Long roomId;
    private String code;
    private String guestName;
    private String guestEmail;
    private String guestGender;
    private String phoneNumber;
    private String company;
    private String address;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
}
