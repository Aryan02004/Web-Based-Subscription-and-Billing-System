package com.app.notification.template;

import com.app.customer.entity.CustomerEntity;

public class PaymentFailedEmailTemplate {

	private PaymentFailedEmailTemplate() {
	}

	public static String build(CustomerEntity customer, String reason) {

		return """
				<!DOCTYPE html>
				<html>
				<body style="font-family:Arial;background:#f4f4f4;padding:40px;">

				<div style="max-width:600px;margin:auto;background:white;padding:35px;border-radius:8px;">

				<h2 style="color:#dc2626;">Payment Failed</h2>

				<p>Hello <strong>%s</strong>,</p>

				<p>Unfortunately, we could not process your recent payment.</p>

				<p><strong>Reason:</strong> %s</p>

				<p>Please try again using a different payment method or retry later.</p>

				<br>

				<a href="#"
				style="background:#2563eb;color:white;padding:12px 24px;
				text-decoration:none;border-radius:5px;">
				Retry Payment
				</a>

				<br><br>

				Regards,<br>
				<b>Subscriptor Team</b>

				</div>

				</body>
				</html>
				""".formatted(customer.getFirstName(), reason);
	}
}