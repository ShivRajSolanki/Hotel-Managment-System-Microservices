package com.hotel.bill_service.client;

import com.hotel.bill_service.dto.ReservationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "reservation-service")
public interface ReservationClient {
    @GetMapping("/api/reservations/code/{code}")
    ReservationDto getReservationByCode(@PathVariable String code);
}
