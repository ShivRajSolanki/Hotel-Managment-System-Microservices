package com.hotel.guest_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class GuestResponse {
    private Long id;
    private String memberCode;
    private String name;
    private String email;
    private String phoneNumber;
    private String company;
    private String gender;
    private String address;

}
