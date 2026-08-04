package com.app.invoice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.invoice.entity.InvoiceEntity;
import com.app.invoice.enums.InvoiceStatus;
import com.app.invoice.service.InvoiceService;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService service;

    @PostMapping
    public InvoiceEntity generateInvoice(
            @RequestBody InvoiceEntity invoice) {

        return service.generateInvoice(invoice);
    }

    @GetMapping
    public List<InvoiceEntity> getAllInvoices(@RequestParam(required = false) Long organizationId) {
        return organizationId != null
                ? service.getInvoicesByOrganizationId(organizationId)
                : service.getAllInvoices();
    }

    @GetMapping("/{id}")
    public InvoiceEntity getInvoiceById(
            @PathVariable Long id) {

        return service.getInvoiceById(id);
    }

    @PatchMapping("/{id}/status")
    public InvoiceEntity updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status) {

        return service.updateInvoiceStatus(id, status);
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        return service.downloadInvoice(id);
    }

}