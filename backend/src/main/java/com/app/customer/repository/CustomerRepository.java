package com.app.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.customer.entity.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

	List<CustomerEntity> findByOrganizationId(Long organizationId);

	Optional<CustomerEntity> findByIdAndOrganizationId(Long id, Long organizationId);

	Optional<CustomerEntity> findByOrganizationIdAndEmail(Long organizationId, String email);
}