package com.app.customer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.auth.entity.User;
import com.app.auth.repository.UserRepository;
import com.app.customer.entity.CustomerEntity;
import com.app.customer.repository.CustomerRepository;
import com.app.invoice.email.EmailService;
import com.app.notification.enums.NotificationChannel;
import com.app.notification.enums.NotificationType;
import com.app.notification.service.NotificationService;
import com.app.notification.template.WelcomeEmailTemplate;
import com.app.organization.entity.OrganizationUser;
import com.app.organization.repository.OrganizationUserRepository;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository repository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganizationUserRepository organizationUserRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private EmailService emailService;

	private Long getCurrentOrganizationId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String email = authentication.getName();

		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		OrganizationUser organizationUser = organizationUserRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		return organizationUser.getOrganization().getId();
	}

	@Override
	public CustomerEntity createCustomer(CustomerEntity customer) {

		customer.setOrganizationId(getCurrentOrganizationId());

		CustomerEntity savedCustomer = repository.save(customer);
		String html = WelcomeEmailTemplate.build(savedCustomer);

		emailService.sendHtmlEmail(savedCustomer.getEmail(), "Welcome to Subscriptor", html);

		notificationService.createCustomerNotification(savedCustomer.getId(), "Customer Added",
				savedCustomer.getFirstName() + " " + savedCustomer.getLastName() + " has been added successfully.",
				NotificationType.CUSTOMER, NotificationChannel.IN_APP);

		return savedCustomer;
	}

	@Override
	public CustomerEntity createCustomer(CustomerEntity customer, Long organizationId) {

		customer.setOrganizationId(organizationId);

		CustomerEntity savedCustomer = repository.save(customer);

		String html = WelcomeEmailTemplate.build(savedCustomer);

		emailService.sendHtmlEmail(savedCustomer.getEmail(), "Welcome to Subscriptor", html);

		notificationService.createCustomerNotification(savedCustomer.getId(), "Customer Added",
				savedCustomer.getFirstName() + " " + savedCustomer.getLastName() + " has been added successfully.",
				NotificationType.CUSTOMER, NotificationChannel.IN_APP);

		return savedCustomer;
	}

	@Override
	public List<CustomerEntity> getAllCustomers() {

		return repository.findByOrganizationId(getCurrentOrganizationId());
	}

	@Override
	public CustomerEntity getCustomerById(Long id) {

		return repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Customer not found"));
	}

	@Override
	public CustomerEntity updateCustomer(Long id, CustomerEntity customer) {

		CustomerEntity existingCustomer = repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		existingCustomer.setFirstName(customer.getFirstName());
		existingCustomer.setLastName(customer.getLastName());
		existingCustomer.setEmail(customer.getEmail());
		existingCustomer.setPhone(customer.getPhone());

		return repository.save(existingCustomer);
	}

	@Override
	public void deleteCustomer(Long id) {

		CustomerEntity customer = repository.findByIdAndOrganizationId(id, getCurrentOrganizationId())
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		repository.delete(customer);
	}
}