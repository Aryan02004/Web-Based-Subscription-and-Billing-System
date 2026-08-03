package com.app.payment.razorpay;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.payment.entity.Payment;
import com.app.payment.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class RazorpayService {

	@Value("${razorpay.key.id}")
	private String keyId;

	@Value("${razorpay.key.secret}")
	private String keySecret;

	@Autowired
	private PaymentRepository paymentRepository;

	public String createOrder(Long paymentId) {

		try {

			Payment payment = paymentRepository.findById(paymentId)
					.orElseThrow(() -> new RuntimeException("Payment not found"));

			RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

			JSONObject orderRequest = new JSONObject();

			orderRequest.put("amount", payment.getAmount().multiply(BigDecimal.valueOf(100)).intValue());

			orderRequest.put("currency", payment.getCurrency());

			orderRequest.put("receipt", "PAY_" + payment.getId());

			Order order = razorpay.orders.create(orderRequest);

			payment.setRazorpayOrderId(order.get("id"));

			paymentRepository.save(payment);

			return order.toString();

		} catch (Exception e) {
			throw new RuntimeException("Failed to create Razorpay Order", e);
		}

	}

	public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {

		try {

			JSONObject options = new JSONObject();

			options.put("razorpay_order_id", razorpayOrderId);
			options.put("razorpay_payment_id", razorpayPaymentId);
			options.put("razorpay_signature", razorpaySignature);

			return Utils.verifyPaymentSignature(options, keySecret);

		} catch (Exception e) {
			throw new RuntimeException("Payment verification failed", e);
		}
	}
}