package com.app.notification.template;

import com.app.customer.entity.CustomerEntity;

public class WelcomeEmailTemplate {

	private WelcomeEmailTemplate() {
	}

	public static String build(CustomerEntity customer) {

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

				            <table width="600" cellpadding="0" cellspacing="0"
				                   style="background:#ffffff;border-radius:10px;padding:40px;">

				                <tr>
				                    <td align="center">
				                        <h2 style="color:#2563eb;">
				                            Welcome to Subscriptor
				                        </h2>
				                    </td>
				                </tr>

				                <tr>
				                    <td>

				                        <p>Hello <strong>%s</strong>,</p>

				                        <p>
				                            Welcome! Your account has been created successfully.
				                        </p>

				                        <p>
				                            We are excited to have you with us and look forward
				                            to helping you manage your subscriptions with ease.
				                        </p>

				                        <hr>

				                        <p style="font-size:13px;color:#888;">
				                            Regards,<br>
				                            <strong>Subscriptor Team</strong>
				                        </p>

				                    </td>
				                </tr>

				            </table>

				        </td>
				    </tr>
				</table>

				</body>
				</html>
				""".formatted(customer.getFirstName());
	}
}