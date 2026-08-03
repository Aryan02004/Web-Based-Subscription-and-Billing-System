package com.app.billing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingCheckoutRequest {

    private Long planId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}