package com.app.payment.service;

import java.util.List;

import com.app.payment.entity.Payment;
import com.app.payment.request.RazorpayVerificationRequest;

public interface PaymentService {

	Payment createPayment(Payment payment);

	List<Payment> getAllPayments();

	Payment getPaymentById(Long id);

	Payment updatePayment(Long id, Payment payment);

	Payment verifyPayment(Long paymentId, RazorpayVerificationRequest request);

}