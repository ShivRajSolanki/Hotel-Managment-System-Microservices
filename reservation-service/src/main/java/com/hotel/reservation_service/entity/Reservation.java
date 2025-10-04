package com.hotel.reservation_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
