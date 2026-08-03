package com.app.invoice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.invoice.entity.InvoiceEntity;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    List<InvoiceEntity> findBySubscriptionOrganizationId(Long organizationId);

    Optional<InvoiceEntity> findByIdAndSubscriptionOrganizationId(Long id, Long organizationId);

}