package com.app.invoice.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.app.customer.entity.CustomerEntity;
import com.app.customer.repository.CustomerRepository;
import com.app.invoice.entity.InvoiceEntity;
import com.app.organization.entity.Organization;
import com.app.organization.repository.OrganizationRepository;
import com.app.subscriptionplan.entity.SubscriptionPlan;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component
public class PdfGenerator {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	public byte[] generateInvoicePdf(InvoiceEntity invoice) {

		try {

			CustomerEntity customer = customerRepository.findById(invoice.getSubscription().getCustomerId())
					.orElseThrow(() -> new RuntimeException("Customer not found"));

			Organization organization = organizationRepository.findById(invoice.getSubscription().getOrganizationId())
					.orElseThrow(() -> new RuntimeException("Organization not found"));

			SubscriptionPlan plan = invoice.getSubscription().getPlan();

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4, 35, 35, 35, 35);

			PdfWriter.getInstance(document, out);

			document.open();

			// ---------------- COLORS ----------------//

			Color primaryBlue = new Color(37, 99, 235);

			Color lightBlue = new Color(239, 246, 255);

			Color lightGrey = new Color(248, 250, 252);

			Color borderGrey = new Color(220, 220, 220);

			Color successGreen = new Color(22, 163, 74);

			// ---------------- FONTS ----------------//

			Font logoFont = new Font(Font.HELVETICA, 26, Font.BOLD, Color.WHITE);

			Font subLogoFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.WHITE);

			Font headingFont = new Font(Font.HELVETICA, 14, Font.BOLD);

			Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);

			Font normalFont = new Font(Font.HELVETICA, 10);

			Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);

			Font smallFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);

			Font statusFont = new Font(Font.HELVETICA, 11, Font.BOLD, successGreen);

			// ---------------- HEADER ----------------//

			PdfPTable header = new PdfPTable(1);

			header.setWidthPercentage(100);

			PdfPCell head = new PdfPCell();

			head.setBackgroundColor(primaryBlue);

			head.setBorder(Rectangle.NO_BORDER);

			head.setPaddingTop(18);

			head.setPaddingBottom(18);

			Paragraph logo = new Paragraph("SUBSCRIPTOR", logoFont);

			logo.setAlignment(Element.ALIGN_CENTER);

			head.addElement(logo);

			Paragraph subLogo = new Paragraph("Subscription & Billing Platform", subLogoFont);

			subLogo.setAlignment(Element.ALIGN_CENTER);

			head.addElement(subLogo);

			Paragraph invoiceTitle = new Paragraph("SUBSCRIPTION INVOICE",
					new Font(Font.HELVETICA, 15, Font.BOLD, Color.WHITE));

			invoiceTitle.setAlignment(Element.ALIGN_CENTER);

			invoiceTitle.setSpacingBefore(8);

			head.addElement(invoiceTitle);

			header.addCell(head);

			document.add(header);

			document.add(new Paragraph(" "));

			// ---------------- ORGANIZATION ----------------//

			Paragraph issued = new Paragraph("Issued For", smallFont);

			document.add(issued);

			Paragraph org = new Paragraph(organization.getName(), titleFont);

			org.setSpacingAfter(15);

			document.add(org);

			// ---------------- INVOICE INFO ----------------//

			PdfPTable info = new PdfPTable(3);

			info.setWidthPercentage(100);

			info.setWidths(new float[] { 33, 33, 34 });

			String invoiceDate = invoice.getInvoiceDate() == null ? "-"
					: invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
			PdfPCell cell1 = createInfoCard("Invoice Number", invoice.getInvoiceNumber(), lightGrey, borderGrey,
					smallFont, boldFont);

			PdfPCell cell2 = createInfoCard("Invoice Date", invoiceDate, lightGrey, borderGrey, smallFont, boldFont);

			PdfPCell cell3 = createInfoCard("Payment Status", "● " + invoice.getStatus(), lightGrey, borderGrey,
					smallFont, statusFont);

			info.addCell(cell1);
			info.addCell(cell2);
			info.addCell(cell3);

			document.add(info);

			document.add(new Paragraph(" "));

			// ==================== BILL TO & SUBSCRIPTION ====================//

			PdfPTable body = new PdfPTable(2);

			body.setWidthPercentage(100);

			body.setSpacingBefore(10);

			body.setSpacingAfter(20);

			body.setWidths(new float[] { 45, 55 });

			// ==================== LEFT CARD ====================//

			PdfPCell customerCard = new PdfPCell();

			customerCard.setPadding(15);

			customerCard.setBorderColor(borderGrey);

			customerCard.setBackgroundColor(Color.WHITE);

			customerCard.addElement(new Paragraph("BILL TO", headingFont));

			customerCard.addElement(new Paragraph(" "));

			String customerName = customer.getFirstName();

			if (customer.getLastName() != null && !customer.getLastName().isBlank()) {

				customerName += " " + customer.getLastName();

			}

			customerCard.addElement(new Paragraph(customerName, boldFont));

			customerCard.addElement(new Paragraph(customer.getEmail(), normalFont));

			customerCard.addElement(new Paragraph(customer.getPhone() == null ? "-" : customer.getPhone(), normalFont));

			body.addCell(customerCard);

			// ==================== RIGHT CARD ====================//

			PdfPCell subscriptionCard = new PdfPCell();

			subscriptionCard.setPadding(15);

			subscriptionCard.setBorderColor(borderGrey);

			subscriptionCard.setBackgroundColor(lightBlue);

			subscriptionCard.addElement(new Paragraph("SUBSCRIPTION", headingFont));

			subscriptionCard.addElement(new Paragraph(" "));

			subscriptionCard.addElement(new Paragraph(plan.getPlanName(), boldFont));

			subscriptionCard.addElement(new Paragraph(" "));

			if (plan.getDescription() != null) {

				subscriptionCard.addElement(new Paragraph(plan.getDescription(), normalFont));

			}

			subscriptionCard.addElement(new Paragraph(" "));

			String billingCycle = plan.getBillingCycle().name();

			billingCycle = billingCycle.substring(0, 1) + billingCycle.substring(1).toLowerCase();

			subscriptionCard.addElement(new Paragraph(billingCycle + " Subscription", boldFont));

			subscriptionCard.addElement(new Paragraph(" "));

//			Paragraph featureHeading = new Paragraph("Included Features", headingFont);
//
//			subscriptionCard.addElement(featureHeading);
//
//			subscriptionCard.addElement(new Paragraph(" "));
//
//			Map<String, Object> features = plan.getFeatures();
//
//			if (features != null) {
//
//				for (Map.Entry<String, Object> entry : features.entrySet()) {
//
//					subscriptionCard.addElement(
//
//							new Paragraph(
//
//									"✔ " + entry.getKey() + " : " + entry.getValue(),
//
//									normalFont)
//
//					);
//
//				}
//
//			}

			body.addCell(subscriptionCard);

			document.add(body);
			// ==================== AMOUNT DETAILS ====================//

			Paragraph amountTitle = new Paragraph("PAYMENT SUMMARY", headingFont);

			amountTitle.setSpacingBefore(10);
			amountTitle.setSpacingAfter(10);

			document.add(amountTitle);

			PdfPTable amountTable = new PdfPTable(4);

			amountTable.setWidthPercentage(100);

			amountTable.setWidths(new float[] { 50, 10, 20, 20 });

			amountTable.setSpacingAfter(20);

			addTableHeader(amountTable, "Description");
			addTableHeader(amountTable, "Qty");
			addTableHeader(amountTable, "Unit Price");
			addTableHeader(amountTable, "Amount");

			addTableCell(amountTable, plan.getPlanName());

			addTableCell(amountTable, "1");

			addTableCell(amountTable, invoice.getCurrency() + " " + invoice.getSubtotal());

			addTableCell(amountTable, invoice.getCurrency() + " " + invoice.getSubtotal());

			addTableCell(amountTable, "GST (18%)");

			addTableCell(amountTable, "");

			addTableCell(amountTable, "");

			addTableCell(amountTable, invoice.getCurrency() + " " + invoice.getTaxAmount());

			document.add(amountTable);

			// ==================== TOTAL CARD ====================//

			PdfPTable totalTable = new PdfPTable(2);

			totalTable.setWidthPercentage(40);

			totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

			totalTable.setSpacingAfter(20);

			totalTable.setWidths(new float[] { 50, 50 });

			addSummaryRow(totalTable, "Subtotal", invoice.getCurrency() + " " + invoice.getSubtotal(), false);

			addSummaryRow(totalTable, "GST", invoice.getCurrency() + " " + invoice.getTaxAmount(), false);

			addSummaryRow(totalTable, "TOTAL", invoice.getCurrency() + " " + invoice.getTotalAmount(), true);

			document.add(totalTable);

			// ==================== SUBSCRIPTION PERIOD ====================//

			Paragraph periodHeading = new Paragraph("SUBSCRIPTION PERIOD", headingFont);

			periodHeading.setSpacingAfter(8);

			document.add(periodHeading);

			PdfPTable periodTable = new PdfPTable(2);

			periodTable.setWidthPercentage(100);

			periodTable.setWidths(new float[] { 50, 50 });

			periodTable.setSpacingAfter(20);

			String startDate = invoice.getSubscription().getStartDate()
					.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

			String endDate = invoice.getSubscription().getEndDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

			PdfPCell start = new PdfPCell();

			start.setPadding(12);

			start.setBackgroundColor(lightGrey);

			start.addElement(new Paragraph("Start Date", smallFont));

			start.addElement(new Paragraph(startDate, boldFont));

			periodTable.addCell(start);

			PdfPCell end = new PdfPCell();

			end.setPadding(12);

			end.setBackgroundColor(lightGrey);

			end.addElement(new Paragraph("End Date", smallFont));

			end.addElement(new Paragraph(endDate, boldFont));

			periodTable.addCell(end);

			document.add(periodTable);
			// ==================== PAYMENT ====================//

			Paragraph paymentHeading = new Paragraph("PAYMENT DETAILS", headingFont);

			paymentHeading.setSpacingAfter(8);

			document.add(paymentHeading);

			PdfPTable paymentTable = new PdfPTable(2);

			paymentTable.setWidthPercentage(100);

			paymentTable.setWidths(new float[] { 50, 50 });

			paymentTable.setSpacingAfter(20);

			PdfPCell paymentMethod = new PdfPCell();

			paymentMethod.setPadding(12);

			paymentMethod.setBackgroundColor(lightGrey);

			paymentMethod.setBorderColor(borderGrey);

			paymentMethod.addElement(new Paragraph("Payment Method", smallFont));

			paymentMethod.addElement(new Paragraph("UPI", boldFont));

			paymentTable.addCell(paymentMethod);

			PdfPCell paymentStatus = new PdfPCell();

			paymentStatus.setPadding(12);

			paymentStatus.setBackgroundColor(lightGrey);

			paymentStatus.setBorderColor(borderGrey);

			paymentStatus.addElement(new Paragraph("Payment Status", smallFont));

			paymentStatus.addElement(new Paragraph("● SUCCESS", statusFont));

			paymentTable.addCell(paymentStatus);

			document.add(paymentTable);

			// ==================== SUPPORT ====================//

			Paragraph support = new Paragraph("SUPPORT", headingFont);

			support.setSpacingAfter(8);

			document.add(support);

			document.add(new Paragraph("For billing related queries please contact:", normalFont));

			document.add(new Paragraph(organization.getContactEmail(), boldFont));

			document.add(new Paragraph(" "));

			// ==================== TERMS ====================//

			Paragraph terms = new Paragraph("TERMS & CONDITIONS", headingFont);

			terms.setSpacingAfter(8);

			document.add(terms);

			document.add(new Paragraph("• This invoice confirms successful payment.", normalFont));

			document.add(new Paragraph("• Subscription is valid only for the purchased billing period.", normalFont));

			document.add(new Paragraph("• Refunds are subject to the organization's refund policy.", normalFont));

			document.add(new Paragraph("• Please retain this invoice for future reference.", normalFont));

			document.add(new Paragraph("• This invoice has been generated electronically by Subscriptor.", normalFont));

			document.add(new Paragraph(" "));

			// ==================== FOOTER ====================//

			PdfPTable footer = new PdfPTable(1);

			footer.setWidthPercentage(100);

			PdfPCell footerCell = new PdfPCell();

			footerCell.setBackgroundColor(primaryBlue);

			footerCell.setBorder(Rectangle.NO_BORDER);

			footerCell.setPadding(15);

			Paragraph thankYou = new Paragraph("Thank you for choosing " + organization.getName(),
					new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE));

			thankYou.setAlignment(Element.ALIGN_CENTER);

			footerCell.addElement(thankYou);

			Paragraph powered = new Paragraph("Powered by Subscriptor",
					new Font(Font.HELVETICA, 10, Font.NORMAL, Color.WHITE));

			powered.setAlignment(Element.ALIGN_CENTER);

			powered.setSpacingBefore(8);

			footerCell.addElement(powered);

			footer.addCell(footerCell);

			document.add(footer);

			document.close();

			return out.toByteArray();

		} catch (Exception e) {

			throw new RuntimeException("Failed to generate PDF", e);
		}
	}

	// ========================================================

	private PdfPCell createInfoCard(String title, String value, Color background, Color border, Font titleFont,
			Font valueFont) {

		PdfPCell cell = new PdfPCell();

		cell.setPadding(10);

		cell.setBackgroundColor(background);

		cell.setBorderColor(border);

		cell.addElement(new Paragraph(title, titleFont));

		cell.addElement(new Paragraph(value, valueFont));

		return cell;

	}

	// ========================================================

	private void addTableHeader(PdfPTable table, String text) {

		Font font = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

		PdfPCell cell = new PdfPCell(new Phrase(text, font));

		cell.setBackgroundColor(new Color(37, 99, 235));

		cell.setHorizontalAlignment(Element.ALIGN_CENTER);

		cell.setPadding(8);

		table.addCell(cell);

	}

	// ========================================================

	private void addTableCell(PdfPTable table, String text) {

		PdfPCell cell = new PdfPCell(new Phrase(text));

		cell.setPadding(8);

		table.addCell(cell);

	}

	// ========================================================

	private void addSummaryRow(PdfPTable table, String label, String value, boolean total) {

		Font font;

		PdfPCell left = new PdfPCell();

		PdfPCell right = new PdfPCell();

		if (total) {

			font = new Font(Font.HELVETICA, 11, Font.BOLD);

			left.setBackgroundColor(new Color(219, 234, 254));

			right.setBackgroundColor(new Color(219, 234, 254));

		} else {

			font = new Font(Font.HELVETICA, 10);

			left.setBackgroundColor(Color.WHITE);

			right.setBackgroundColor(Color.WHITE);

		}

		left.setBorder(Rectangle.NO_BORDER);

		right.setBorder(Rectangle.NO_BORDER);

		left.setPadding(8);

		right.setPadding(8);

		right.setHorizontalAlignment(Element.ALIGN_RIGHT);

		left.setPhrase(new Phrase(label, font));

		right.setPhrase(new Phrase(value, font));

		table.addCell(left);

		table.addCell(right);

	}
}
