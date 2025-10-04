package com.hotel.room_service.service;

import com.hotel.room_service.dto.RoomRequest;
import com.hotel.room_service.dto.RoomResponse;
import com.hotel.room_service.entity.Room;
import com.hotel.room_service.exception.RoomAlreadyExistsException;
import com.hotel.room_service.exception.RoomNotFoundException;
import com.hotel.room_service.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomResponse addRoom(RoomRequest request) {
        log.info("Attempting to add new room: {}", request.getRoomNumber());

        roomRepository.findByRoomNumber(request.getRoomNumber()).ifPresent(room -> {
            log.warn("Room with number {} already exists", request.getRoomNumber());
            throw new RoomAlreadyExistsException("Room number already exists: " + request.getRoomNumber());
        });

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .capacity(request.getCapacity())
                .pricePerNight(request.getPricePerNight())
                .isAvailable(request.isAvailable())
                .build();

        Room savedRoom = roomRepository.save(room);
        log.info("Room added with ID: {}", savedRoom.getId());

        return toResponse(savedRoom);
    }

    public List<RoomResponse> getAvailableRooms(int guests) {
        log.info("Retrieving available rooms for at least {} guests", guests);
        return roomRepository.findByCapacityGreaterThanEqualAndIsAvailableTrue(guests)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse updateRoom(Long id, RoomRequest request) {
        log.info("Updating room with ID: {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Room not found for update: ID {}", id);
                    return new RoomNotFoundException("Room not found with ID: " + id);
                });

        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setPricePerNight(request.getPricePerNight());
        room.setAvailable(request.isAvailable());

        Room updatedRoom = roomRepository.save(room);
        log.info("Room updated: ID {}", updatedRoom.getId());

        return toResponse(updatedRoom);
    }

    public void deleteRoom(Long id) {
        log.warn("Deleting room with ID: {}", id);
        roomRepository.deleteById(id);
        log.info("Room deleted: ID {}", id);
    }

    public RoomResponse getRoom(Long id) {
        log.debug("Retrieving room by ID: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Room not found with ID: {}", id);
                    return new RoomNotFoundException("Room not found with ID: " + id);
                });

        return toResponse(room);
    }

    public List<RoomResponse> getAllRooms() {
        log.debug("Fetching all rooms");
        return roomRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse updateRoomAvailability(Long id, boolean available) {
        log.info("Updating availability for room ID {} to {}", id, available);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Room not found for availability update: ID {}", id);
                    return new RoomNotFoundException("Room not found with ID: " + id);
                });

        room.setAvailable(available);
        Room updated = roomRepository.save(room);
        log.info("Availability updated for room {}: now {}", updated.getRoomNumber(), available);

        return toResponse(updated);
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getCapacity(),
                room.getPricePerNight(),
                room.isAvailable()
        );
    }
}