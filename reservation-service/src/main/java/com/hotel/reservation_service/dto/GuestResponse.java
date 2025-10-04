package com.hotel.reservation_service.dto;

import lombok.Data;

@Data
public class GuestResponse {
    private Long id;
    private String memberCode;
    private String phoneNumber;
    private String company;
    private String name;
    private String email;
    private String gender;
    private String address;
}
