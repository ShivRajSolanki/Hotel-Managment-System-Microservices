package com.hotel.room_service.service;
import com.hotel.room_service.dto.RoomRequest;
import com.hotel.room_service.dto.RoomResponse;
import com.hotel.room_service.entity.Room;
import com.hotel.room_service.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private RoomRequest request;

    @BeforeEach
    void setup() {
        request = new RoomRequest("A101", "Deluxe", 2, 1800.0, true);
    }

    @Test
    void shouldAddRoomSuccessfully() {
        when(roomRepository.findByRoomNumber("A101")).thenReturn(Optional.empty());

        Room mockRoom = Room.builder()
                .id(1L)
                .roomNumber("A101")
                .roomType("Deluxe")
                .capacity(2)
                .pricePerNight(1800.0)
                .isAvailable(true)
                .build();

        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);

        RoomResponse response = roomService.addRoom(request);

        assertEquals("A101", response.getRoomNumber());
        assertTrue(response.isAvailable());
    }

    @Test
    void shouldThrowIfRoomNumberExists() {
        when(roomRepository.findByRoomNumber("A101")).thenReturn(Optional.of(new Room()));

        assertThrows(IllegalArgumentException.class, () -> roomService.addRoom(request));
    }

    @Test
    void shouldReturnAvailableRooms() {
        Room availableRoom = Room.builder()
                .id(2L)
                .roomNumber("A102")
                .roomType("Suite")
                .capacity(3)
                .pricePerNight(2200.0)
                .isAvailable(true)
                .build();

        when(roomRepository.findByCapacityGreaterThanEqualAndIsAvailableTrue(2))
                .thenReturn(List.of(availableRoom));

        List<RoomResponse> results = roomService.getAvailableRooms(2);

        assertEquals(1, results.size());
        assertEquals("Suite", results.get(0).getRoomType());
    }

    @Test
    void shouldUpdateRoomDetails() {
        Room existing = Room.builder()
                .id(3L)
                .roomNumber("A103")
                .roomType("Standard")
                .capacity(2)
                .pricePerNight(1000.0)
                .isAvailable(true)
                .build();

        when(roomRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        RoomRequest update = new RoomRequest("A103", "Standard", 2, 1100.0, false);

        RoomResponse updated = roomService.updateRoom(3L, update);

        assertEquals(1100.0, updated.getPricePerNight());
        assertFalse(updated.isAvailable());
    }

    @Test
    void shouldDeleteRoom() {
        doNothing().when(roomRepository).deleteById(4L);

        roomService.deleteRoom(4L);

        verify(roomRepository).deleteById(4L);
    }

    @Test
    void shouldGetRoomById() {
        Room room = Room.builder()
                .id(5L)
                .roomNumber("A104")
                .roomType("Executive")
                .capacity(2)
                .pricePerNight(2000.0)
                .isAvailable(true)
                .build();

        when(roomRepository.findById(5L)).thenReturn(Optional.of(room));

        RoomResponse result = roomService.getRoom(5L);

        assertEquals("A104", result.getRoomNumber());
        assertTrue(result.isAvailable());
    }

    @Test
    void shouldGetAllRooms() {
        Room room = Room.builder()
                .id(6L)
                .roomNumber("A105")
                .roomType("Premium")
                .capacity(4)
                .pricePerNight(3500.0)
                .isAvailable(true)
                .build();

        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomResponse> allRooms = roomService.getAllRooms();

        assertEquals(1, allRooms.size());
        assertEquals("Premium", allRooms.get(0).getRoomType());
    }

    @Test
    void shouldUpdateAvailabilityStatus() {
        Room room = Room.builder()
                .id(7L)
                .roomNumber("A106")
                .roomType("Studio")
                .capacity(1)
                .pricePerNight(800.0)
                .isAvailable(true)
                .build();

        when(roomRepository.findById(7L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse response = roomService.updateRoomAvailability(7L, false);

        assertFalse(response.isAvailable());
    }
}
