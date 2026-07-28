package com.app.invoice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.invoice.entity.InvoiceEntity;
import com.app.invoice.enums.InvoiceStatus;
import com.app.invoice.pdf.PdfGenerator;
import com.app.invoice.repository.InvoiceRepository;
import com.app.invoice.util.InvoiceNumberGenerator;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationUserRepository;
import com.app.subscription.entity.SubscriptionEntity;
import com.app.subscription.repository.SubscriptionRepository;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	private InvoiceRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganizationUserRepository organizationUserRepository;

	@Autowired
	private InvoiceNumberGenerator invoiceNumberGenerator;

	@Autowired
	private PdfGenerator pdfGenerator;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	private Long getCurrentOrganizationId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return organizationUser.getOrganization().getId();
	}

	@Override
	public InvoiceEntity generateInvoice(InvoiceEntity invoice) {

		Long currentOrgId = getCurrentOrganizationId();

		SubscriptionEntity subscription = subscriptionRepository
				.findByIdAndOrganizationId(invoice.getSubscription().getId(), currentOrgId)
				.orElseThrow(() -> new RuntimeException("Subscription not found or unauthorized"));

		invoice.setSubscription(subscription);

		invoice.setGeneratedAt(LocalDateTime.now());

		if (invoice.getStatus() == null) {
			invoice.setStatus(InvoiceStatus.PENDING);
		}

		invoice.setInvoiceNumber(invoiceNumberGenerator.generateInvoiceNumber());

		InvoiceEntity savedInvoice = repository.save(invoice);

		// Generate PDF
		byte[] pdf = pdfGenerator.generateInvoicePdf(savedInvoice);

		// Email will be sent here in next step
		// emailService.sendInvoice(savedInvoice, pdf);

		return savedInvoice;
	}

	@Override
	public List<InvoiceEntity> getAllInvoices() {

		return repository.findBySubscriptionOrganizationId(getCurrentOrganizationId());
	}

	@Override
	public InvoiceEntity getInvoiceById(Long id) {

		return repository.findByIdAndSubscriptionOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Invoice not found"));
	}

	@Override
	public InvoiceEntity updateInvoiceStatus(Long id, InvoiceStatus status) {

		InvoiceEntity invoice = repository.findByIdAndSubscriptionOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Invoice not found"));

		invoice.setStatus(status);

		return repository.save(invoice);
	}

	@Override
	public ResponseEntity<byte[]> downloadInvoice(Long id) {

		InvoiceEntity invoice = repository.findByIdAndSubscriptionOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Invoice not found"));

		byte[] pdf = pdfGenerator.generateInvoicePdf(invoice);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + invoice.getInvoiceNumber() + ".pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdf);
	}

}