package com.hotel.reservation_service.repository;

import com.hotel.reservation_service.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCheckInDateLessThanAndCheckOutDateGreaterThan(
            LocalDate checkOutDate, LocalDate checkInDate
    );
}