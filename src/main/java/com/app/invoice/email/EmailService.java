package com.app.invoice.email;

import java.io.ByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.app.customer.repository.CustomerRepository;
import com.app.invoice.entity.InvoiceEntity;
import com.app.organization.repository.OrganizationRepository;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	public void sendInvoice(InvoiceEntity invoice, byte[] pdf) {

		try {

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom("subscriptorr@gmail.com");
			// TODO: Replace with actual customer email
			Long customerId = invoice.getSubscription().getCustomerId();

			var customer = customerRepository.findById(customerId)
					.orElseThrow(() -> new RuntimeException("Customer not found"));

			String organizationName = organizationRepository.findById(invoice.getSubscription().getOrganizationId())
					.orElseThrow(() -> new RuntimeException("Organization not found")).getName();
			helper.setTo(customer.getEmail());

			helper.setSubject(organizationName + " - Invoice " + invoice.getInvoiceNumber());

			String html = """
					<!DOCTYPE html>
					<html>
					<body style="font-family:Arial,sans-serif;background:#f4f4f4;padding:30px;">

					<div style="max-width:600px;margin:auto;background:#ffffff;padding:30px;border-radius:10px;">

					<h2 style="color:#2563eb;">Invoice Attached</h2>

					<p>Hi <strong>%s</strong>,</p>

					<p>
					Thank you for subscribing to <strong>%s</strong>.
					</p>

					<p>
					Your payment has been received successfully.
					</p>

					<p>
					Please find your invoice attached with this email.
					</p>

					<p>

					<b>Invoice Number:</b> %s<br>
					<b>Plan:</b> %s<br>
					<b>Amount Paid:</b> ₹%s

					</p>

					<hr>

					<p style="font-size:13px;color:#666;">

					Regards,<br>

					<strong>%s</strong><br><br>

					Powered by <strong>Subscriptor</strong>

					</p>

					</div>

					</body>
					</html>
					""".formatted(

					customer.getFirstName(), organizationName, invoice.getInvoiceNumber(),
					invoice.getSubscription().getPlan().getPlanName(), invoice.getTotalAmount(), organizationName);

			helper.setText(html, true);

			helper.addAttachment(invoice.getInvoiceNumber() + ".pdf", new InputStreamSource() {
				@Override
				public java.io.InputStream getInputStream() {
					return new ByteArrayInputStream(pdf);
				}
			});
			System.out.println("Customer ID : " + customerId);
			System.out.println("Sending invoice to : " + customer.getEmail());
			mailSender.send(message);

			System.out.println(customer.getEmail());
		} catch (Exception e) {
			throw new RuntimeException("Failed to send email", e);
		}
	}

	public void sendSimpleEmail(String to, String subject, String body) {

		try {

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, false);

			helper.setFrom("subscriptorr@gmail.com");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body);

			mailSender.send(message);

		} catch (Exception e) {
			throw new RuntimeException("Failed to send email", e);
		}
	}

	public void sendHtmlEmail(String to, String subject, String htmlBody) {

		try {

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom("subscriptorr@gmail.com");
			helper.setTo(to);
			helper.setSubject(subject);

			helper.setText(htmlBody, true);

			mailSender.send(message);

		} catch (Exception e) {
			throw new RuntimeException("Failed to send HTML email", e);
		}
	}
}