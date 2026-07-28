package com.app.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.app.common.entity.BaseEntity;
import com.app.invoice.entity.InvoiceEntity;
import com.app.payment.enums.PaymentMethod.PaymentMethod;
import com.app.payment.enums.PaymentStatus.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_id", nullable = false)
	private InvoiceEntity invoice;

	@Column(name = "payment_reference")
	private String paymentReference;

	@Column(name = "transaction_id")
	private String transactionId;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method")
	private PaymentMethod paymentMethod;

	@Column(precision = 12, scale = 2)
	private BigDecimal amount;

	@Column(length = 10)
	private String currency;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PaymentStatus status;

	@Column(name = "failure_reason")
	private String failureReason;

	@Column(name = "processed_at")
	private LocalDateTime processedAt;

	@Column(name = "razorpay_order_id")
	private String razorpayOrderId;
	
	@Column(name = "razorpay_payment_id")
	private String razorpayPaymentId;
}