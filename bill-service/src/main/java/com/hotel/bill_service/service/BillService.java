package com.hotel.bill_service.service;

import com.hotel.bill_service.client.ReservationClient;
import com.hotel.bill_service.dto.BillResponseDto;
import com.hotel.bill_service.dto.ReservationDto;
import com.hotel.bill_service.entity.Bill;
import com.hotel.bill_service.exception.ReservationNotFoundException;
import com.hotel.bill_service.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillService {

    private final ReservationClient reservationClient;
    private final BillRepository billRepository;
    private final PDFGenerator pdfGenerator;
    private final EmailService emailService;

    public BillResponseDto generateBill(String reservationCode) {
        // Fetch reservation details using external client
        ReservationDto reservation = reservationClient.getReservationByCode(reservationCode);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation not found for code: " + reservationCode);
        }

        // Calculate stay duration
        long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        double totalAmount = reservation.getRate() * nights;

        // Create and save Bill entity
        Bill bill = Bill.builder()
                .reservationCode(reservation.getCode())
                .guestName(reservation.getGuestName())
                .guestEmail(reservation.getGuestEmail())
                .roomNumber(reservation.getRoomNumber())
                .roomType(reservation.getRoomType())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .rate(reservation.getRate())
                .numberOfNights((int) nights)
                .totalAmount(totalAmount)
                .billDate(LocalDateTime.now())
                .build();

        bill = billRepository.save(bill);

        // Generate PDF and send email
        File pdfFile = pdfGenerator.generate(bill);
        emailService.sendBillEmail(bill.getGuestEmail(), pdfFile, bill.getReservationCode());

        // Prepare response DTO
        return BillResponseDto.builder()
                .billId(bill.getBillId())
                .totalAmount(bill.getTotalAmount())
                .message("Bill generated and emailed successfully.")
                .build();
    }

    public Bill getBillById(UUID billId) {
        return billRepository.findById(billId).orElse(null);
    }
}