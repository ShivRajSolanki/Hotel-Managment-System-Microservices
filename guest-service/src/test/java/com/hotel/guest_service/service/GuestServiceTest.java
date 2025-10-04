package com.hotel.guest_service.service;

import com.hotel.guest_service.dto.GuestRequest;
import com.hotel.guest_service.dto.GuestResponse;
import com.hotel.guest_service.entity.Guest;
import com.hotel.guest_service.exception.GuestNotFoundException;
import com.hotel.guest_service.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private GuestService guestService;

    private GuestRequest guestRequest;

    @BeforeEach
    void setup() {
        guestRequest = new GuestRequest();
        guestRequest.setName("Ravi");
        guestRequest.setEmail("ravi@mail.com");
        guestRequest.setPhoneNumber("1234567890");
        guestRequest.setCompany("Acme");
        guestRequest.setGender("Male");
        guestRequest.setAddress("Mumbai");
    }

    @Test
    void shouldAddGuest() {
        when(guestRepository.existsByEmail("ravi@mail.com")).thenReturn(false);
        when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> {
            Guest g = invocation.getArgument(0);
            g.setId(1L);
            return g;
        });

        GuestResponse response = guestService.addGuest(guestRequest);

        assertNotNull(response);
        assertEquals("Ravi", response.getName());
        assertNotNull(response.getMemberCode());
        assertEquals("ravi@mail.com", response.getEmail());

        verify(guestRepository).save(any(Guest.class));
    }

    @Test
    void shouldThrowIfNameOrEmailIsNull() {
        guestRequest.setName(null);

        assertThrows(IllegalArgumentException.class, () -> guestService.addGuest(guestRequest));

        guestRequest.setName("Test");
        guestRequest.setEmail(null);

        assertThrows(IllegalArgumentException.class, () -> guestService.addGuest(guestRequest));
    }

    @Test
    void shouldThrowIfEmailAlreadyExists() {
        when(guestRepository.existsByEmail("ravi@mail.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> guestService.addGuest(guestRequest));
    }

    @Test
    void shouldReturnAllGuests() {
        Guest guest = Guest.builder()
                .id(1L)
                .name("Ravi")
                .email("ravi@mail.com")
                .memberCode("MBR-001")
                .build();

        when(guestRepository.findAll()).thenReturn(List.of(guest));

        List<GuestResponse> guests = guestService.getAllGuests();

        assertEquals(1, guests.size());
        assertEquals("Ravi", guests.get(0).getName());
    }

    @Test
    void shouldFindGuestById() {
        Guest guest = Guest.builder()
                .id(2L)
                .name("Kiran")
                .email("kiran@mail.com")
                .memberCode("MBR-002")
                .build();

        when(guestRepository.findById(2L)).thenReturn(Optional.of(guest));

        GuestResponse response = guestService.getGuestById(2L);

        assertNotNull(response);
        assertEquals("Kiran", response.getName());
    }

    @Test
    void shouldThrowIfGuestNotFoundById() {
        when(guestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GuestNotFoundException.class, () -> guestService.getGuestById(99L));
    }

    @Test
    void shouldDeleteGuestIfExists() {
        when(guestRepository.existsById(3L)).thenReturn(true);
        doNothing().when(guestRepository).deleteById(3L);

        guestService.deleteGuest(3L);

        verify(guestRepository).deleteById(3L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentGuest() {
        when(guestRepository.existsById(404L)).thenReturn(false);

        assertThrows(GuestNotFoundException.class, () -> guestService.deleteGuest(404L));
    }

    @Test
    void shouldUpdateExistingGuest() {
        Guest guest = Guest.builder()
                .id(5L)
                .name("Old Name")
                .email("old@mail.com")
                .memberCode("MBR-009")
                .build();

        when(guestRepository.findById(5L)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(inv -> inv.getArgument(0));

        GuestResponse updated = guestService.updateGuest(5L, guestRequest);

        assertEquals("Ravi", updated.getName());
        assertEquals("ravi@mail.com", updated.getEmail());
        assertEquals("MBR-009", updated.getMemberCode());
    }

    @Test
    void shouldGetGuestByMemberCode() {
        Guest guest = Guest.builder()
                .id(6L)
                .name("Sam")
                .email("sam@mail.com")
                .memberCode("MBR-XYZ123")
                .build();

        when(guestRepository.findByMemberCode("MBR-XYZ123")).thenReturn(Optional.of(guest));

        GuestResponse result = guestService.getGuestByMemberCode("MBR-XYZ123");

        assertEquals("Sam", result.getName());
        assertEquals("MBR-XYZ123", result.getMemberCode());
    }

    @Test
    void shouldThrowIfMemberCodeNotFound() {
        when(guestRepository.findByMemberCode("NON-EXIST")).thenReturn(Optional.empty());

        assertThrows(GuestNotFoundException.class, () -> guestService.getGuestByMemberCode("NON-EXIST"));
    }
}