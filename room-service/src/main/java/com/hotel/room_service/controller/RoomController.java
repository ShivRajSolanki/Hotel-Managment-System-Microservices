package com.hotel.room_service.controller;

import com.hotel.room_service.dto.RoomRequest;
import com.hotel.room_service.dto.RoomResponse;
import com.hotel.room_service.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> addRoom(@Valid @RequestBody RoomRequest request) {
        log.info("Adding new room: {}", request.getRoomNumber());
        return ResponseEntity.ok(roomService.addRoom(request));
    }

    @GetMapping("/availability")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(@RequestParam int guests) {
        log.info("Fetching available rooms for {} guests", guests);
        return ResponseEntity.ok(roomService.getAvailableRooms(guests));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        log.info("Updating room with ID: {}", id);
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        log.warn("Deleting room with ID: {}", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable Long id) {
        log.info("Retrieving room with ID: {}", id);
        return ResponseEntity.ok(roomService.getRoom(id));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        log.debug("Fetching all rooms");
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<RoomResponse> updateAvailability(@PathVariable Long id, @RequestParam("available") boolean available) {
        log.info("Updating availability for room ID {} to {}", id, available);
        return ResponseEntity.ok(roomService.updateRoomAvailability(id, available));
    }
}