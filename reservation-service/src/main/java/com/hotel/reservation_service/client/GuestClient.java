package com.hotel.reservation_service.client;

import com.hotel.reservation_service.dto.GuestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "guest-service")
public interface GuestClient {
    @GetMapping("/api/guests/{id}")
    GuestResponse getGuestById(@PathVariable Long id);
}


