package com.app.payment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.payment.entity.Payment;
import com.app.payment.razorpay.RazorpayService;
import com.app.payment.request.RazorpayVerificationRequest;
import com.app.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	@Autowired
	private RazorpayService razorpayService;

	@Autowired
	private PaymentService paymentService;

	@PostMapping
	public Payment createPayment(@RequestBody Payment payment) {
		return paymentService.createPayment(payment);
	}

	@GetMapping
	public List<Payment> getAllPayments() {
		return paymentService.getAllPayments();
	}

	@GetMapping("/{id}")
	public Payment getPaymentById(@PathVariable Long id) {
		return paymentService.getPaymentById(id);
	}

	@PutMapping("/{id}")
	public Payment updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
		return paymentService.updatePayment(id, payment);
	}

	@PostMapping("/{paymentId}/create-order")
	public String createOrder(@PathVariable Long paymentId) {

		return razorpayService.createOrder(paymentId);
	}

	@PatchMapping("/{id}/verify")
	public Payment verifyPayment(@PathVariable Long id, @RequestBody RazorpayVerificationRequest request) {

		return paymentService.verifyPayment(id, request);
	}
}