package com.hotel.reservation_service.client;

import com.hotel.reservation_service.dto.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "room-service/api")
public interface RoomClient {
//    @GetMapping("/api/rooms/availability")
//    List<RoomResponse> getAvailableRooms(@RequestParam int guests);

    @GetMapping("/rooms")
    List<RoomResponse> getAllRooms();

    @PutMapping("/rooms/{id}/availability")
    void updateRoomAvailability(@PathVariable Long id, @RequestParam("available") boolean available);

}