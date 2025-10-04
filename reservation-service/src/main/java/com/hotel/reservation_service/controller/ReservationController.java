package com.hotel.reservation_service.controller;

import com.hotel.reservation_service.dto.ReservationRequest;
import com.hotel.reservation_service.dto.ReservationResponse;
import com.hotel.reservation_service.dto.RoomResponse;
import com.hotel.reservation_service.dto.RoomSearchRequest;
import com.hotel.reservation_service.service.ReservationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservation Management", description = "Manage reservation")
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/search-rooms")
    public ResponseEntity<List<RoomResponse>> searchRooms(@Valid @RequestBody RoomSearchRequest request) {
        log.info("Searching for available rooms with request: {}", request);
        return ResponseEntity.ok(reservationService.searchAvailableRooms(request));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> makeReservation(@Valid @RequestBody ReservationRequest request) {
        log.info("Making reservation for guest ID: {} in room ID: {}", request.getGuestId(), request.getRoomId());
        return ResponseEntity.ok(reservationService.makeReservation(request));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        log.debug("Retrieving all reservations");
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        log.info("Fetching reservation by ID: {}", id);
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ReservationResponse> getReservationByCode(@PathVariable String code) {
        log.info("Fetching reservation by code: {}", code);
        return ResponseEntity.ok(reservationService.getByCode(code));
    }

    @PutMapping("/{roomId}/availability")
    public ResponseEntity<String> updateRoomAvailabilityViaReservation(
            @PathVariable Long roomId, @RequestParam("available") boolean available) {
        log.warn("Updating room {} availability to {}", roomId, available);
        reservationService.updateRoomAvailability(roomId, available);
        return ResponseEntity.ok("Room availability updated via Reservation-Service.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelReservation(@PathVariable @Min(1) Long id) {
        log.warn("Cancelling reservation with ID: {}", id);
        reservationService.cancelReservation(id);
        return ResponseEntity.ok("Reservation cancelled successfully.");
    }
}