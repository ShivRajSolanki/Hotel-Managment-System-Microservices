//package com.hotel.bill_service.service;
//
//import com.hotel.bill_service.entity.Bill;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.PdfWriter;
//import org.springframework.stereotype.Component;
//import java.io.File;
//import java.io.FileOutputStream;
//
//@Component
//public class PDFGenerator {
//
//    public File generate(Bill bill) {
//        try {
//            Document doc = new Document();
//            File file = File.createTempFile("bill_" + bill.getBillId(), ".pdf");
//            PdfWriter.getInstance(doc, new FileOutputStream(file));
//            doc.open();
//
//            doc.add(new Paragraph("HOTEL INVOICE"));
//            doc.add(new Paragraph("Reservation Code: " + bill.getReservationCode()));
//            doc.add(new Paragraph("Guest: " + bill.getGuestName() + " (" + bill.getGuestEmail() + ")"));
//            doc.add(new Paragraph("Room: " + bill.getRoomNumber() + " - " + bill.getRoomType()));
//            doc.add(new Paragraph("Check-in: " + bill.getCheckInDate()));
//            doc.add(new Paragraph("Check-out: " + bill.getCheckOutDate()));
//            doc.add(new Paragraph("Rate: ₹" + bill.getRate()));
//            doc.add(new Paragraph("Nights: " + bill.getNumberOfNights()));
//            doc.add(new Paragraph("Total: ₹" + bill.getTotalAmount()));
//            doc.add(new Paragraph("Bill Date: " + bill.getBillDate()));
//
//            doc.close();
//            return file;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate PDF", e);
//        }
//    }
//}


package com.hotel.bill_service.service;

import com.hotel.bill_service.entity.Bill;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class PDFGenerator {

    public File generate(Bill bill) {
        try {
            Document doc = new Document(PageSize.A4);
            File file = File.createTempFile("bill_" + bill.getBillId(), ".pdf");
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Title
            Paragraph title = new Paragraph("HOTEL INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            doc.add(title);

            // Guest & Reservation Details Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{1.5f, 3f});

            addRow(table, "Reservation Code:", bill.getReservationCode(), labelFont, valueFont);
            addRow(table, "Guest Name:", bill.getGuestName(), labelFont, valueFont);
            addRow(table, "Guest Email:", bill.getGuestEmail(), labelFont, valueFont);
            addRow(table, "Room:", bill.getRoomNumber() + " - " + bill.getRoomType(), labelFont, valueFont);
            addRow(table, "Check-in Date:", bill.getCheckInDate().toString(), labelFont, valueFont);
            addRow(table, "Check-out Date:", bill.getCheckOutDate().toString(), labelFont, valueFont);
            addRow(table, "Rate per Night:", "₹" + bill.getRate(), labelFont, valueFont);
            addRow(table, "Nights Stayed:", String.valueOf(bill.getNumberOfNights()), labelFont, valueFont);
            addRow(table, "Total Amount:", "₹" + bill.getTotalAmount(), labelFont, valueFont);
            addRow(table, "Bill Date:", bill.getBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), labelFont, valueFont);

            doc.add(table);

            doc.close();
            return file;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}