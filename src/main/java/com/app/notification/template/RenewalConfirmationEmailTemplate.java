package com.app.notification.template;

import com.app.customer.entity.CustomerEntity;

public class RenewalConfirmationEmailTemplate {

	private RenewalConfirmationEmailTemplate() {
	}

	public static String build(CustomerEntity customer) {

		return """
				<!DOCTYPE html>
				<html>
				<body style="font-family:Arial;background:#f4f4f4;padding:40px;">

				<div style="max-width:600px;margin:auto;background:white;padding:35px;border-radius:8px;">

				<h2 style="color:#16a34a;">Subscription Renewed</h2>

				<p>Hello <strong>%s</strong>,</p>

				<p>Your subscription has been renewed successfully.</p>

				<p>Thank you for continuing with us.</p>

				<br>

				Regards,<br>
				<b>Subscriptor Team</b>

				</div>

				</body>
				</html>
				""".formatted(customer.getFirstName());
	}
}