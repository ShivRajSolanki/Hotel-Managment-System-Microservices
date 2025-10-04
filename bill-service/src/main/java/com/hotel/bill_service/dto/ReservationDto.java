package com.hotel.bill_service.dto;

import lombok.Data;

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
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
    private String roomType;
    private double rate;
    private String roomNumber;
}
