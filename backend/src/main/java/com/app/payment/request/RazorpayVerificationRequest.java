package com.app.payment.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RazorpayVerificationRequest {

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

}