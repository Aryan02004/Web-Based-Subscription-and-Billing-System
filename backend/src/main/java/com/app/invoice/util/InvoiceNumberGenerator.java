package com.app.invoice.util;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.invoice.repository.InvoiceRepository;

@Component
public class InvoiceNumberGenerator {

    private final InvoiceRepository invoiceRepository;
    
    public InvoiceNumberGenerator(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public String generateInvoiceNumber() {

        long count = invoiceRepository.count() + 1;

        return String.format(
                "INV-%d-%06d",
                Year.now().getValue(),
                count);
    }
}