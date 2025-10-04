package com.hotel.bill_service.controller;

import com.hotel.bill_service.dto.BillRequestDto;
import com.hotel.bill_service.dto.BillResponseDto;
import com.hotel.bill_service.entity.Bill;
import com.hotel.bill_service.service.BillService;
import com.hotel.bill_service.service.PDFGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;
    private PDFGenerator pdfGenerator;

    @PostMapping
    public ResponseEntity<BillResponseDto> createBill(@Valid @RequestBody BillRequestDto request) {
        return ResponseEntity.ok(billService.generateBill(request.getReservationCode()));
    }

    @GetMapping(value = "/pdf/{billId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadBillPdf(@PathVariable UUID billId) throws IOException {
        Bill bill = billService.getBillById(billId);
        if (bill == null) {
            return ResponseEntity.notFound().build();
        }

        File pdfFile = pdfGenerator.generate(bill);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill-" + billId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfFile.length())
                .body(resource);
    }

}
