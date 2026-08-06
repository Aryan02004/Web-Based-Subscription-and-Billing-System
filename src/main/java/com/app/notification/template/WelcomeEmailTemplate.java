package com.app.notification.template;

import com.app.customer.entity.CustomerEntity;

public class WelcomeEmailTemplate {

	private WelcomeEmailTemplate() {
	}

	public static String build(CustomerEntity customer, String organizationName) {

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

				<h2 style="color:#2563eb;">
				Welcome to %s
				</h2>

				</td>
				</tr>

				<tr>
				<td>

				<p>Hi <strong>%s</strong>,</p>

				<p>
				Thank you for choosing <strong>%s</strong>.
				</p>

				<p>
				Your details have been successfully registered.
				</p>

				<p>
				Complete your payment to activate your subscription and start enjoying our services.
				</p>

				<p>
				We're excited to have you on board.
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
				""".formatted(organizationName, customer.getFirstName(), organizationName, organizationName);
	}
}