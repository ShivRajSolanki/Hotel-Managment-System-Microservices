package com.hotel.bill_service.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBillEmail(String to, File pdfFile, String reservationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Your Hotel Bill - Reservation " + reservationCode);
            helper.setText("Dear Guest,\n\nPlease find attached your invoice.\n\nThank you!");

            FileSystemResource file = new FileSystemResource(pdfFile);
            helper.addAttachment("invoice.pdf", file);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send bill email", e);
        }
    }
}
