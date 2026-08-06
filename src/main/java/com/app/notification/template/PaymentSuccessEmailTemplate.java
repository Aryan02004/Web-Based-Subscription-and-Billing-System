package com.app.notification.template;

import com.app.customer.entity.CustomerEntity;

public class PaymentSuccessEmailTemplate {

	private PaymentSuccessEmailTemplate() {
	}

	public static String build(CustomerEntity customer, String organizationName, String planName, String amount) {

		return """
								<!DOCTYPE html>
								<html>
								<head>
								<meta charset="UTF-8">
								</head>

								<body style="margin:0;padding:0;background:#f4f4f4;font-family:Arial,sans-serif;">

								<table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px;">
								<tr>
								<td align="center">

								<table width="600" cellpadding="20" cellspacing="0"
								style="background:#ffffff;border-radius:10px;">

								<tr>
								<td align="center">

								<h2 style="color:#16a34a;">
				                Your Subscription is Active
				                </h2>

								</td>
								</tr>

								<tr>
								<td>

								<p>Hi <strong>%s</strong>,</p>

								<p>
								Thank you for your payment.
								</p>

								<p>
								Your payment of <strong>₹%s</strong> has been received successfully.
								</p>

								<p>
								<b>Organization:</b> %s<br>
								<b>Plan:</b> %s
								</p>

								<p>
								Your subscription has been activated successfully.
								</p>

								<p>
								Your invoice has been attached with a separate email for your reference.
								</p>

								<hr>

								<p style="font-size:13px;color:#666;">

								Regards,<br>

								<strong>%s</strong><br><br>

								Powered by <strong>Subscriptor</strong>

								</p>

								</td>
								</tr>

								</table>

								</td>
								</tr>
								</table>

								</body>
								</html>
								""".formatted(customer.getFirstName(), amount, organizationName, planName,
				organizationName);
	}
}