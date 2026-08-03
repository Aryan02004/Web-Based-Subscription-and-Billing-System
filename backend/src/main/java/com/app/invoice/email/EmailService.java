package com.app.invoice.email;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.invoice.entity.InvoiceEntity;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserRepository userRepository;

	public void sendInvoice(InvoiceEntity invoice, byte[] pdf) {

		try {

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom("subscriptorr@gmail.com");
			// TODO: Replace with actual customer email
			Long customerId = invoice.getSubscription().getCustomerId();

			User customer = userRepository.findById(customerId)
					.orElseThrow(() -> new RuntimeException("Customer not found"));

			helper.setTo("laddhasanket705@gmail.com");

			helper.setSubject("Invoice " + invoice.getInvoiceNumber());

			helper.setText("Dear Customer,\n\n" + "Please find your invoice attached.\n\n" + "Thank you.");

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
}