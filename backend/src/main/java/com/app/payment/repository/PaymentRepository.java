package com.app.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceSubscriptionOrganizationId(Long organizationId);

    Optional<Payment> findByIdAndInvoiceSubscriptionOrganizationId(
            Long id,
            Long organizationId);

}