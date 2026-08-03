package com.app.invoice.pdf;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Component;

import com.app.invoice.entity.InvoiceEntity;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Component
public class PdfGenerator {

    public byte[] generateInvoicePdf(InvoiceEntity invoice) {

        try {

            Document document = new Document();

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);

            document.open();

            Font title = new Font(Font.HELVETICA, 18, Font.BOLD);

            document.add(new Paragraph("INVOICE", title));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Invoice No : " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Invoice Date : " + invoice.getInvoiceDate()));
            document.add(new Paragraph("Due Date : " + invoice.getDueDate()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Plan : " + invoice.getSubscription().getPlan().getPlanName()));
            document.add(new Paragraph("Amount : " + invoice.getSubtotal()));
            document.add(new Paragraph("Tax : " + invoice.getTaxAmount()));
            document.add(new Paragraph("Total : " + invoice.getTotalAmount()));
            document.add(new Paragraph("Currency : " + invoice.getCurrency()));
            document.add(new Paragraph("Status : " + invoice.getStatus()));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}