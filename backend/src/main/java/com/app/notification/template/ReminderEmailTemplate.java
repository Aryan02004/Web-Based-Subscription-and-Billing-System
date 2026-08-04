package com.app.notification.template;

import com.app.customer.entity.CustomerEntity;

public class ReminderEmailTemplate {

	private ReminderEmailTemplate() {
	}

	public static String build(CustomerEntity customer, String message) {

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
				                            Subscription Expiry Reminder
				                        </h2>
				                    </td>
				                </tr>

				                <tr>
				                    <td>

				                        <p>Hello <strong>%s</strong>,</p>

				                        <p>%s</p>

				                        <p>
				                            Please renew your subscription before the expiry date
				                            to continue enjoying uninterrupted service.
				                        </p>

				                        <div style="text-align:center;margin:35px 0;">
				                            <a href="#"
				                               style="
				                               background:#2563eb;
				                               color:white;
				                               text-decoration:none;
				                               padding:14px 30px;
				                               border-radius:6px;
				                               display:inline-block;
				                               font-weight:bold;">
				                                Renew Subscription
				                            </a>
				                        </div>

				                        <p style="color:#666;">
				                            If you've already renewed your subscription,
				                            you may safely ignore this email.
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
				""".formatted(customer.getFirstName(), message);
	}
}