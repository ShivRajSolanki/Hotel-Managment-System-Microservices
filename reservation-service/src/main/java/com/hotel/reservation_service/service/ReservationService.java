package com.hotel.reservation_service.service;

import com.hotel.reservation_service.client.GuestClient;
import com.hotel.reservation_service.client.RoomClient;
import com.hotel.reservation_service.dto.*;
import com.hotel.reservation_service.entity.Reservation;
import com.hotel.reservation_service.exceptions.RoomNotAvailableException;
import com.hotel.reservation_service.exceptions.RoomNotFoundException;
import com.hotel.reservation_service.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomClient roomClient;
    private final GuestClient guestClient;

    public List<RoomResponse> searchAvailableRooms(RoomSearchRequest request) {
        log.info("Searching for available rooms: guests={}, checkIn={}, checkOut={}",
                request.getNumberOfGuests(), request.getCheckInDate(), request.getCheckOutDate());

        List<RoomResponse> allRooms = roomClient.getAllRooms();

        List<Reservation> reservedRooms = reservationRepository.findAll().stream()
                .filter(r -> datesOverlap(r.getCheckInDate(), r.getCheckOutDate(), request.getCheckInDate(), request.getCheckOutDate()))
                .toList();

        Set<Long> reservedRoomIds = reservedRooms.stream()
                .map(Reservation::getRoomId)
                .collect(Collectors.toSet());

        log.debug("Rooms already reserved for selected dates: {}", reservedRoomIds);

        List<RoomResponse> unreservedRooms = allRooms.stream()
                .filter(room -> !reservedRoomIds.contains(room.getId()))
                .toList();

        List<RoomResponse> result = unreservedRooms.stream()
                .filter(room -> room.getCapacity() == request.getNumberOfGuests())
                .filter(room-> room.getRoomType().equals(request.getRoomType()))
                .collect(Collectors.toList());

        log.info("Found {} available rooms for request", result.size());
        return result;
    }

    private boolean datesOverlap(LocalDate existingStart, LocalDate existingEnd, LocalDate newStart, LocalDate newEnd) {
        return !(existingEnd.isBefore(newStart) || existingStart.isAfter(newEnd.minusDays(1)));
    }

    private String generateReservationCode() {
        String code = "RESV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.debug("Generated reservation code: {}", code);
        return code;
    }

    public ReservationResponse makeReservation(ReservationRequest request) {
        log.info("Initiating reservation for guestId={}, roomId={}", request.getGuestId(), request.getRoomId());

        GuestResponse guest = guestClient.getGuestById(request.getGuestId());
        log.info("Validated guest: {} ({})", guest.getName(), guest.getEmail());

        List<Reservation> conflictingReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getRoomId().equals(request.getRoomId()) &&
                        datesOverlap(r.getCheckInDate(), r.getCheckOutDate(), request.getCheckInDate(), request.getCheckOutDate()))
                .toList();

        if (!conflictingReservations.isEmpty()) {
            log.error("Conflict detected: room {} already reserved between {} and {}",
                    request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());
            throw new RoomNotAvailableException("Room is already reserved for the selected dates.");
        }

        RoomResponse room = roomClient.getAllRooms().stream()
                .filter(r -> r.getId().equals(request.getRoomId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Room {} not found while making reservation", request.getRoomId());
                    return new RoomNotFoundException("Room not available");
                });

        int totalGuests = request.getNumberOfAdults() + request.getNumberOfChildren();
        if (room.getCapacity() < totalGuests) {
            log.warn("Room {} has capacity {}, which is insufficient for {} guests",
                    room.getRoomNumber(), room.getCapacity(), totalGuests);
            throw new RoomNotAvailableException("Room capacity is insufficient for the number of guests.");
        }

        if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            log.error("Invalid date range: check-out {} is before check-in {}",
                    request.getCheckOutDate(), request.getCheckInDate());
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        Reservation reservation = Reservation.builder()
                .code(generateReservationCode())
                .numberOfChildren(request.getNumberOfChildren())
                .numberOfAdults(request.getNumberOfAdults())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfNights((int) ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate()))
                .status("Confirmed")
                .guestId(guest.getId())
                .roomId(room.getId())
                .build();

        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation saved: ID={}, Code={}", saved.getId(), saved.getCode());

        roomClient.updateRoomAvailability(room.getId(), false);
        log.info("Room {} marked as unavailable", room.getRoomNumber());

        return toResponse(saved);
    }

    public List<ReservationResponse> getAllReservations() {
        log.debug("Fetching all reservations");
        List<ReservationResponse> reservations = reservationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        log.info("Retrieved {} reservations", reservations.size());
        return reservations;
    }

    private ReservationResponse toResponse(Reservation reservation) {
        GuestResponse guest = null;
        try {
            guest = guestClient.getGuestById(reservation.getGuestId());
        } catch (Exception e) {
            log.warn("Failed to fetch guest {} for reservation ID {}: {}", reservation.getGuestId(), reservation.getId(), e.getMessage());
        }

        RoomResponse room = roomClient.getAllRooms().stream()
                .filter(r -> r.getId().equals(reservation.getRoomId()))
                .findFirst()
                .orElse(null);

        if (room == null) {
            log.warn("Room {} not found for reservation ID {}. Returning partial reservation data.", reservation.getRoomId(), reservation.getId());
        }

//                .orElseThrow(() -> {
//                    log.error("Room {} not found while mapping reservation ID {}", reservation.getRoomId(), reservation.getId());
//                    return new RoomNotFoundException("Room not found for reservation");
//                });

        return ReservationResponse.builder()
                .id(reservation.getId())
                .code(reservation.getCode())
                .numberOfChildren(reservation.getNumberOfChildren())
                .numberOfAdults(reservation.getNumberOfAdults())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .status(reservation.getStatus())
                .numberOfNights(reservation.getNumberOfNights())
                .guestId(reservation.getGuestId())
                .roomId(reservation.getRoomId())
                .guestName(guest.getName())
                .guestEmail(guest.getEmail())
//                .roomNumber(room.getRoomNumber())
//                .roomType(room.getRoomType())
//                .rate(room.getPricePerNight())
                .roomNumber(room != null ? room.getRoomNumber() : "N/A")
                .roomType(room != null ? room.getRoomType() : "Unknown")
                .rate(room != null ? room.getPricePerNight() : 0.0)

                .build();
    }

    public void updateRoomAvailability(Long roomId, boolean available) {
        log.info("Evaluating availability update for room ID {} to {}", roomId, available);

        boolean stillReserved = reservationRepository.findAll().stream()
                .anyMatch(reservation -> reservation.getRoomId().equals(roomId) &&
                        reservation.getCheckOutDate().isAfter(LocalDate.now()));

        if (!stillReserved) {
            log.info("Room ID {} has no active reservations. Marking available.", roomId);
            roomClient.updateRoomAvailability(roomId, true);
        } else {
            log.info("Room ID {} is still reserved. Keeping as unavailable.", roomId);
            roomClient.updateRoomAvailability(roomId, false);
        }
    }

    public void cancelReservation(Long id) {
        log.warn("Cancelling reservation ID {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Reservation {} not found for cancellation", id);
                    return new RuntimeException("Reservation not found");
                });

        try {
            roomClient.updateRoomAvailability(reservation.getRoomId(), true);
            log.info("Room {} marked available", reservation.getRoomId());
        } catch (Exception e) {
            log.warn("Failed to update room availability for room {}: {}", reservation.getRoomId(), e.getMessage());
        }

        reservationRepository.deleteById(id);
        log.info("Reservation {} successfully cancelled", reservation.getId());

    }

    public ReservationResponse getByCode(String code) {
        log.debug("Looking up reservation by code: {}", code);

        return reservationRepository.findAll().stream()
                .filter(r -> r.getCode().equals(code))
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("No reservation found for code {}", code);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found for code: " + code);
                });
    }

    public ReservationResponse getReservationById(Long id) {
        log.debug("Fetching reservation by ID: {}", id);

        return reservationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("Reservation not found with ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: ");
                });
    }
}