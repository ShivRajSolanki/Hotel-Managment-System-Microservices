package com.hotel.bill_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillResponseDto {
    private UUID billId;
    private double totalAmount;
    private String message;
}
