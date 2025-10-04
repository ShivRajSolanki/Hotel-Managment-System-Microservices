package com.hotel.reservation_service.service;

import com.hotel.reservation_service.client.GuestClient;
import com.hotel.reservation_service.client.RoomClient;
import com.hotel.reservation_service.dto.*;
import com.hotel.reservation_service.entity.Reservation;
import com.hotel.reservation_service.exceptions.RoomNotAvailableException;
import com.hotel.reservation_service.exceptions.RoomNotFoundException;
import com.hotel.reservation_service.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomClient roomClient;

    @Mock
    private GuestClient guestClient;

    @InjectMocks
    private ReservationService reservationService;

    private GuestResponse guest;
    private RoomResponse room;
    private ReservationRequest request;

    @BeforeEach
    void setUp() {
        guest = new GuestResponse();
        guest.setId(1L);
        guest.setName("John Doe");
        guest.setEmail("john@example.com");

        room = new RoomResponse(2L, "301", "Deluxe", 2, 1200.0, true);

        request = new ReservationRequest();
        request.setGuestId(1L);
        request.setRoomId(2L);
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3));
        request.setNumberOfAdults(2);
        request.setNumberOfChildren(0);
    }

    @Test
    void shouldMakeReservation() {
        when(guestClient.getGuestById(1L)).thenReturn(guest);
        when(roomClient.getAllRooms()).thenReturn(List.of(room));
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> {
                    Reservation r = inv.getArgument(0);
                    r.setId(100L);
                    return r;
                });

        ReservationResponse response = reservationService.makeReservation(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getGuestName());
        verify(roomClient).updateRoomAvailability(2L, false);
    }

    @Test
    void shouldThrowIfRoomAlreadyReserved() {
        Reservation conflicting = Reservation.builder()
                .roomId(2L)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .build();

        when(guestClient.getGuestById(1L)).thenReturn(guest);
        when(reservationRepository.findAll()).thenReturn(List.of(conflicting));

        assertThrows(RoomNotAvailableException.class, () -> reservationService.makeReservation(request));
    }

    @Test
    void shouldThrowIfRoomCapacityIsInsufficient() {
        room.setCapacity(1);

        when(guestClient.getGuestById(1L)).thenReturn(guest);
        when(roomClient.getAllRooms()).thenReturn(List.of(room));
        when(reservationRepository.findAll()).thenReturn(List.of());

        assertThrows(RoomNotAvailableException.class, () -> reservationService.makeReservation(request));
    }

    @Test
    void shouldThrowIfRoomNotFound() {
        when(guestClient.getGuestById(1L)).thenReturn(guest);
        when(roomClient.getAllRooms()).thenReturn(List.of());
        when(reservationRepository.findAll()).thenReturn(List.of());

        assertThrows(RoomNotFoundException.class, () -> reservationService.makeReservation(request));
    }

    @Test
    void shouldSearchAvailableRooms() {
        RoomResponse r1 = new RoomResponse(1L, "A", "Standard", 2, 1000, true);
        RoomResponse r2 = new RoomResponse(2L, "B", "Deluxe", 4, 2000, true);
        RoomSearchRequest search = new RoomSearchRequest();
        search.setCheckInDate(LocalDate.now().plusDays(1));
        search.setCheckOutDate(LocalDate.now().plusDays(3));
        search.setNumberOfGuests(2);

        Reservation existing = Reservation.builder()
                .roomId(1L)
                .checkInDate(search.getCheckInDate())
                .checkOutDate(search.getCheckOutDate())
                .build();

        when(roomClient.getAllRooms()).thenReturn(List.of(r1, r2));
        when(reservationRepository.findAll()).thenReturn(List.of(existing));

        List<RoomResponse> result = reservationService.searchAvailableRooms(search);

        assertEquals(1, result.size());
        assertEquals("B", result.get(0).getRoomNumber());
    }

    @Test
    void shouldCancelReservation() {
        Reservation reservation = Reservation.builder().id(10L).roomId(5L).build();

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));
        doNothing().when(roomClient).updateRoomAvailability(5L, true);

        reservationService.cancelReservation(10L);

        verify(reservationRepository).deleteById(10L);
    }

    @Test
    void shouldUpdateRoomAvailabilityToAvailable() {
        when(reservationRepository.findAll()).thenReturn(List.of());

        reservationService.updateRoomAvailability(10L, true);

        verify(roomClient).updateRoomAvailability(10L, true);
    }

    @Test
    void shouldUpdateRoomAvailabilityToUnavailable() {
        Reservation upcoming = Reservation.builder()
                .roomId(20L)
                .checkOutDate(LocalDate.now().plusDays(2))
                .build();

        when(reservationRepository.findAll()).thenReturn(List.of(upcoming));

        reservationService.updateRoomAvailability(20L, true);

        verify(roomClient).updateRoomAvailability(20L, false);
    }

    @Test
    void shouldGetReservationByCode() {
        Reservation reservation = Reservation.builder()
                .id(3L).code("R-100").guestId(1L).roomId(2L).build();

        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        when(guestClient.getGuestById(1L)).thenReturn(guest);
        when(roomClient.getAllRooms()).thenReturn(List.of(room));

        ReservationResponse response = reservationService.getByCode("R-100");

        assertEquals("R-100", response.getCode());
    }

    @Test
    void shouldThrowIfReservationCodeNotFound() {
        when(reservationRepository.findAll()).thenReturn(List.of());

        assertThrows(ResponseStatusException.class, () -> reservationService.getByCode("INVALID"));
    }

    @Test
    void shouldGetReservationById() {
        Reservation reservation = Reservation.builder()
                .id(999L).guestId(1L).roomId(2L).build();

        when(reservationRepository.findById(999L)).thenReturn(Optional.of(reservation));
        when(guestClient.getGuestById(1L)).thenReturn(guest);
        when(roomClient.getAllRooms()).thenReturn(List.of(room));

        ReservationResponse response = reservationService.getReservationById(999L);

        assertEquals(999L, response.getId());
        assertEquals("301", response.getRoomNumber());
    }

    @Test
    void shouldReturnAllReservations() {
        Reservation r = Reservation.builder().id(1L).guestId(1L).roomId(2L).build();

        when(reservationRepository.findAll()).thenReturn(List.of(r));
        when(guestClient.getGuestById(1L)).thenReturn(guest);
        when(roomClient.getAllRooms()).thenReturn(List.of(room));

        List<ReservationResponse> results = reservationService.getAllReservations();

        assertEquals(1, results.size());
        assertEquals("301", results.get(0).getRoomNumber());
    }
}