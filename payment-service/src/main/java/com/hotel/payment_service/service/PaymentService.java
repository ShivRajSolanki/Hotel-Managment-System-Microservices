package com.hotel.payment_service.service;

import com.hotel.payment_service.dto.RazorPaymentResponse;
import com.hotel.payment_service.entity.Payment;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    Map<String, String> createPaymentOrder(int amount);
    boolean confirmPayment(String orderId, String paymentId, String razorpaySign);

    List<Payment> getAllPayments();
}
