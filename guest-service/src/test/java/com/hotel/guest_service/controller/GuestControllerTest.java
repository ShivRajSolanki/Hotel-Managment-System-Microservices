package com.hotel.guest_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.guest_service.dto.GuestRequest;
import com.hotel.guest_service.dto.GuestResponse;
import com.hotel.guest_service.service.GuestService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuestController.class)
class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GuestService guestService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldAddGuest() throws Exception {
        GuestRequest request = new GuestRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        GuestResponse response = new GuestResponse(1L, "MBR-12345678", "John Doe", "john@example.com", null, null, null, null);

        when(guestService.addGuest(any(GuestRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldGetAllGuests() throws Exception {
        List<GuestResponse> guests = List.of(
                new GuestResponse(1L, "MBR-1", "Alice", "alice@mail.com", null, null, null, null)
        );

        when(guestService.getAllGuests()).thenReturn(guests);

        mockMvc.perform(get("/api/guests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetGuestById() throws Exception {
        GuestResponse guest = new GuestResponse(2L, "MBR-2", "Bob", "bob@mail.com", null, null, null, null);

        when(guestService.getGuestById(2L)).thenReturn(guest);

        mockMvc.perform(get("/api/guests/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"));
    }

    @Test
    void shouldDeleteGuest() throws Exception {
        doNothing().when(guestService).deleteGuest(3L);

        mockMvc.perform(delete("/api/guests/3"))
                .andExpect(status().isNoContent());
    }
}