package com.app.invoice.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.app.invoice.entity.InvoiceEntity;
import com.app.invoice.enums.InvoiceStatus;
import com.app.subscription.entity.SubscriptionEntity;

public interface InvoiceService {

	InvoiceEntity generateInvoice(InvoiceEntity invoice);

	InvoiceEntity generateInvoice(SubscriptionEntity subscription);

	List<InvoiceEntity> getAllInvoices();

	InvoiceEntity getInvoiceById(Long id);

	InvoiceEntity updateInvoiceStatus(Long id, InvoiceStatus status);

	ResponseEntity<byte[]> downloadInvoice(Long id);
}