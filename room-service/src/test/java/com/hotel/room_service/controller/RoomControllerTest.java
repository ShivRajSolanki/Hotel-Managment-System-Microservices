package com.hotel.room_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.room_service.dto.RoomRequest;
import com.hotel.room_service.dto.RoomResponse;
import com.hotel.room_service.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldAddRoom() throws Exception {
        RoomRequest request = new RoomRequest("101", "Deluxe", 2, 1500.0, true);
        RoomResponse response = new RoomResponse(1L, "101", "Deluxe", 2, 1500.0, true);

        when(roomService.addRoom(any(RoomRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("101"));
    }

    @Test
    void shouldGetAllRooms() throws Exception {
        when(roomService.getAllRooms()).thenReturn(List.of(
                new RoomResponse(1L, "102", "Suite", 3, 2500.0, true)
        ));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetAvailableRooms() throws Exception {
        when(roomService.getAvailableRooms(2)).thenReturn(List.of(
                new RoomResponse(1L, "103", "Standard", 2, 1200.0, true)
        ));

        mockMvc.perform(get("/api/rooms/availability?guests=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomType").value("Standard"));
    }

    @Test
    void shouldUpdateRoom() throws Exception {
        RoomRequest request = new RoomRequest("104", "Executive", 2, 1800.0, false);
        RoomResponse updated = new RoomResponse(1L, "104", "Executive", 2, 1800.0, false);

        when(roomService.updateRoom(1L, request)).thenReturn(updated);

        mockMvc.perform(put("/api/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("104"));
    }

    @Test
    void shouldDeleteRoom() throws Exception {
        doNothing().when(roomService).deleteRoom(1L);

        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetRoomById() throws Exception {
        RoomResponse room = new RoomResponse(5L, "105", "Deluxe", 2, 1700.0, true);

        when(roomService.getRoom(5L)).thenReturn(room);

        mockMvc.perform(get("/api/rooms/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("105"));
    }

    @Test
    void shouldUpdateRoomAvailability() throws Exception {
        RoomResponse response = new RoomResponse(10L, "106", "Studio", 1, 900.0, false);

        when(roomService.updateRoomAvailability(10L, false)).thenReturn(response);

        mockMvc.perform(put("/api/rooms/10/availability?available=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }
}
