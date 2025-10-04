package com.hotel.guest_service.controller;

import com.hotel.guest_service.dto.GuestRequest;
import com.hotel.guest_service.dto.GuestResponse;
import com.hotel.guest_service.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
@Slf4j
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PostMapping
    @Operation(summary = "Add Guest", description = "Creates a guest profile.")
    public ResponseEntity<GuestResponse> add(@Valid  @RequestBody GuestRequest guestRequest) {
        log.info("Adding guest: {}", guestRequest);
        GuestResponse response = guestService.addGuest(guestRequest);
        log.info("Guest added successfully: {}", response);
        return ResponseEntity.ok(response);

    }

    @GetMapping
    public ResponseEntity<List<GuestResponse>> getAll() {
        log.info("Fetching all guests.");
        List<GuestResponse> guests = guestService.getAllGuests();
        log.info("Fetched {} guests.", guests.size());
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestResponse> getById(@PathVariable Long id) {
        log.info("Fetching guest with ID: {}", id);
        GuestResponse guestResponse = guestService.getGuestById(id);
        log.info("Guest found: {}", guestResponse);
        return ResponseEntity.ok(guestResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("Deleting guest with ID: {}", id);
        guestService.deleteGuest(id);
        log.info("Guest deleted successfully.");
        return ResponseEntity.noContent().build();
    }
}
