package com.app.payment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.customer.entity.CustomerEntity;
import com.app.customer.repository.CustomerRepository;
import com.app.invoice.email.EmailService;
import com.app.invoice.entity.InvoiceEntity;
import com.app.invoice.enums.InvoiceStatus;
import com.app.invoice.pdf.PdfGenerator;
import com.app.invoice.repository.InvoiceRepository;
import com.app.notification.enums.NotificationChannel;
import com.app.notification.enums.NotificationType;
import com.app.notification.service.NotificationService;
import com.app.notification.template.PaymentFailedEmailTemplate;
import com.app.notification.template.PaymentSuccessEmailTemplate;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationRepository;
import com.app.organization.repository.OrganizationUserRepository;
import com.app.payment.entity.Payment;
import com.app.payment.enums.PaymentStatus.PaymentStatus;
import com.app.payment.razorpay.RazorpayService;
import com.app.payment.repository.PaymentRepository;
import com.app.payment.request.RazorpayVerificationRequest;
import com.app.subscription.entity.SubscriptionEntity;
import com.app.subscription.repository.SubscriptionRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganizationUserRepository organizationUserRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private PdfGenerator pdfGenerator;

	@Autowired
	private EmailService emailService;

	@Autowired
	private RazorpayService razorpayService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private CustomerRepository customerRepository;

	private Long getCurrentOrganizationId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return organizationUser.getOrganization().getId();
	}

	@Override
	public Payment createPayment(Payment payment) {

		InvoiceEntity invoice = invoiceRepository
				.findByIdAndSubscriptionOrganizationId(payment.getInvoice().getId(), getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Invoice not found"));

		payment.setInvoice(invoice);

		payment.setAmount(invoice.getTotalAmount());

		payment.setCurrency(invoice.getCurrency());

		payment.setStatus(PaymentStatus.PENDING);

		return paymentRepository.save(payment);
	}

	@Override
	public Payment createPayment(Payment payment, Long organizationId) {

		InvoiceEntity invoice = invoiceRepository.findById(payment.getInvoice().getId())
				.orElseThrow(() -> new RuntimeException("Invoice not found"));

		payment.setInvoice(invoice);
		payment.setAmount(invoice.getTotalAmount());
		payment.setCurrency(invoice.getCurrency());
		payment.setStatus(PaymentStatus.PENDING);

		return paymentRepository.save(payment);
	}

	@Override
	public List<Payment> getAllPayments() {

		return paymentRepository.findByInvoiceSubscriptionOrganizationId(getCurrentOrganizationId());
	}

	@Override
	public Payment getPaymentById(Long id) {

		return paymentRepository.findByIdAndInvoiceSubscriptionOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Payment not found"));
	}

	@Override
	public Payment updatePayment(Long id, Payment payment) {

		Payment existing = paymentRepository
				.findByIdAndInvoiceSubscriptionOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		existing.setPaymentMethod(payment.getPaymentMethod());
		existing.setPaymentReference(payment.getPaymentReference());
		existing.setTransactionId(payment.getTransactionId());
		existing.setStatus(payment.getStatus());
		existing.setFailureReason(payment.getFailureReason());
		existing.setProcessedAt(LocalDateTime.now());

		if (payment.getStatus() == PaymentStatus.SUCCESS) {
			InvoiceEntity invoice = existing.getInvoice();
			invoice.setStatus(InvoiceStatus.PAID);
			invoiceRepository.save(invoice);
		}

		return paymentRepository.save(existing);
	}

	@Override
	public Payment verifyPayment(Long paymentId, RazorpayVerificationRequest request) {

//		Payment payment = paymentRepository
//				.findByIdAndInvoiceSubscriptionOrganizationId(paymentId, getCurrentOrganizationId())
//				.orElseThrow(() -> new RuntimeException("Payment not found"));

		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		boolean verified = razorpayService.verifyPaymentSignature(request.getRazorpayOrderId(),
				request.getRazorpayPaymentId(), request.getRazorpaySignature());
		if (!verified) {

			payment.setStatus(PaymentStatus.FAILED);
			payment.setFailureReason("Invalid Razorpay Signature");
			payment.setProcessedAt(LocalDateTime.now());

			paymentRepository.save(payment);

			CustomerEntity customer = customerRepository
					.findById(payment.getInvoice().getSubscription().getCustomerId())
					.orElseThrow(() -> new RuntimeException("Customer not found"));

			String html = PaymentFailedEmailTemplate.build(customer, payment.getFailureReason());

			emailService.sendHtmlEmail(customer.getEmail(), "Payment Failed", html);

			throw new RuntimeException("Invalid Razorpay Signature");
		}

		payment.setRazorpayOrderId(request.getRazorpayOrderId());
		payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
		payment.setStatus(PaymentStatus.SUCCESS);
		payment.setProcessedAt(LocalDateTime.now());

		InvoiceEntity invoice = payment.getInvoice();
		invoice.setStatus(InvoiceStatus.PAID);
		invoiceRepository.save(invoice);

		SubscriptionEntity subscription = invoice.getSubscription();

		subscription.setStatus("ACTIVE");
		subscriptionRepository.save(subscription);

		CustomerEntity customer = customerRepository.findById(subscription.getCustomerId())
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		String organizationName = organizationRepository.findById(subscription.getOrganizationId())
				.orElseThrow(() -> new RuntimeException("Organization not found")).getName();

		String planName = subscription.getPlan().getPlanName();

		String html = PaymentSuccessEmailTemplate.build(customer, organizationName, planName,
				payment.getAmount().toString());

		emailService.sendHtmlEmail(customer.getEmail(), organizationName + " - Payment Successful", html);

		notificationService.createCustomerNotification(subscription.getCustomerId(), "Payment Successful",
				"Payment of ₹" + payment.getAmount() + " received for Invoice #" + invoice.getId(),
				NotificationType.PAYMENT, NotificationChannel.IN_APP);

		notificationService.createCustomerNotification(subscription.getCustomerId(), "Subscription Activated",
				"Subscription has been activated successfully.", NotificationType.SUBSCRIPTION,
				NotificationChannel.IN_APP);

		byte[] pdf = pdfGenerator.generateInvoicePdf(invoice);
		System.out.println("========== SENDING EMAIL ==========");
		emailService.sendInvoice(invoice, pdf);
		System.out.println("========== EMAIL SENT ==========");

		return paymentRepository.save(payment);
	}
}