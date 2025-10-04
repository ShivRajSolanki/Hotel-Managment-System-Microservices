package com.hotel.reservation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.reservation_service.dto.*;
import com.hotel.reservation_service.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSearchRooms() throws Exception {
        RoomSearchRequest request = new RoomSearchRequest();
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(2));
        request.setNumberOfGuests(2);

        RoomResponse room = new RoomResponse(1L, "101", "Deluxe", 2, 1200.0, true);

        when(reservationService.searchAvailableRooms(any(RoomSearchRequest.class)))
                .thenReturn(List.of(room));

        mockMvc.perform(post("/api/reservations/search-rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNumber").value("101"));
    }

    @Test
    void shouldMakeReservation() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setGuestId(1L);
        request.setRoomId(2L);
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3));
        request.setNumberOfAdults(2);
        request.setNumberOfChildren(1);

        ReservationResponse response = ReservationResponse.builder()
                .id(10L)
                .code("RSV-0001")
                .numberOfAdults(2)
                .numberOfChildren(1)
                .guestId(1L)
                .roomId(2L)
                .status("CONFIRMED")
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfNights(2)
                .guestName("John Doe")
                .roomNumber("201")
                .roomType("Deluxe")
                .rate(1500.0)
                .build();

        when(reservationService.makeReservation(any(ReservationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("RSV-0001"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void shouldGetAllReservations() throws Exception {
        ReservationResponse reservation = ReservationResponse.builder()
                .id(1L)
                .code("RSV-0002")
                .status("CONFIRMED")
                .guestId(2L)
                .roomId(3L)
                .build();

        when(reservationService.getAllReservations()).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("RSV-0002"));
    }

    @Test
    void shouldGetReservationById() throws Exception {
        ReservationResponse reservation = ReservationResponse.builder()
                .id(3L)
                .code("RSV-0003")
                .status("CONFIRMED")
                .build();

        when(reservationService.getReservationById(3L)).thenReturn(reservation);

        mockMvc.perform(get("/api/reservations/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("RSV-0003"));
    }

    @Test
    void shouldGetReservationByCode() throws Exception {
        ReservationResponse reservation = ReservationResponse.builder()
                .id(4L)
                .code("RSV-2024")
                .guestId(7L)
                .build();

        when(reservationService.getByCode("RSV-2024")).thenReturn(reservation);

        mockMvc.perform(get("/api/reservations/code/RSV-2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("RSV-2024"));
    }

    @Test
    void shouldUpdateRoomAvailabilityViaReservation() throws Exception {
        doNothing().when(reservationService).updateRoomAvailability(5L, false);

        mockMvc.perform(put("/api/reservations/5/availability?available=false"))
                .andExpect(status().isOk())
                .andExpect(content().string("Room availability updated via Reservation-Service."));
    }

    @Test
    void shouldCancelReservation() throws Exception {
        doNothing().when(reservationService).cancelReservation(6L);

        mockMvc.perform(delete("/api/reservations/6"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reservation cancelled successfully."));
    }
}
