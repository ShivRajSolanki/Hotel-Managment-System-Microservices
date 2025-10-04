package com.hotel.guest_service.service;

import com.hotel.guest_service.dto.GuestRequest;
import com.hotel.guest_service.dto.GuestResponse;
import com.hotel.guest_service.entity.Guest;
import com.hotel.guest_service.exception.GuestNotFoundException;
import com.hotel.guest_service.repository.GuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Transactional
    public GuestResponse addGuest(GuestRequest request) {
        if (request.getName() == null || request.getEmail() == null) {
            throw new IllegalArgumentException("Name and email are required.");
        }

        if (guestRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        Guest guest = new Guest();
        guest.setMemberCode(generateMemberCode());
        guest.setName(request.getName());
        guest.setEmail(request.getEmail());
        guest.setPhoneNumber(request.getPhoneNumber());
        guest.setCompany(request.getCompany());
        guest.setGender(request.getGender());
        guest.setAddress(request.getAddress());

        Guest saved = guestRepository.save(guest);
        return mapToGuestResponse(saved);
    }

    public List<GuestResponse> getAllGuests() {
        List<Guest> guests = guestRepository.findAll();
        return guests.stream()
                .map(this::mapToGuestResponse)
                .collect(Collectors.toList());
    }

    public GuestResponse getGuestById(Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found with ID: " + id));
        return mapToGuestResponse(guest);
    }

    public GuestResponse getGuestByMemberCode(String memberCode) {
        Guest guest = guestRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found with member code: " + memberCode));
        return mapToGuestResponse(guest);
    }

    @Transactional
    public void deleteGuest(Long id) {
        if (!guestRepository.existsById(id)) {
            throw new GuestNotFoundException("Guest not found with ID: " + id);
        }
        guestRepository.deleteById(id);
    }

    @Transactional
    public GuestResponse updateGuest(Long id, GuestRequest request) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found with ID: " + id));

        guest.setName(request.getName());
        guest.setEmail(request.getEmail());
        guest.setPhoneNumber(request.getPhoneNumber());
        guest.setCompany(request.getCompany());
        guest.setGender(request.getGender());
        guest.setAddress(request.getAddress());

        Guest updated = guestRepository.save(guest);
        return mapToGuestResponse(updated);
    }

    private String generateMemberCode() {
        return "MBR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private GuestResponse mapToGuestResponse(Guest guest) {
        return new GuestResponse(
                guest.getId(),
                guest.getMemberCode(),
                guest.getName(),
                guest.getEmail(),
                guest.getPhoneNumber(),
                guest.getCompany(),
                guest.getGender(),
                guest.getAddress()
        );
    }
}