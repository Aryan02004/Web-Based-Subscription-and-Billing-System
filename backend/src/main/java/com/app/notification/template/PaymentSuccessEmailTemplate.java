package com.app.notification.template;

import com.app.customer.entity.CustomerEntity;

public class PaymentSuccessEmailTemplate {

	private PaymentSuccessEmailTemplate() {
	}

	public static String build(CustomerEntity customer, String amount) {

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
				                        <h2 style="color:#16a34a;">
				                            Payment Successful
				                        </h2>
				                    </td>
				                </tr>

				                <tr>
				                    <td>

				                        <p>Hello <strong>%s</strong>,</p>

				                        <p>
				                            We have successfully received your payment of
				                            <strong>₹%s</strong>.
				                        </p>

				                        <p>
				                            Your subscription is now active and you can continue
				                            using the service without interruption.
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
				""".formatted(customer.getFirstName(), amount);
	}
}