package com.app.billing;

import java.util.List;

import com.app.subscriptionplan.entity.SubscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BillingResponse {

    private String organizationName;

    private List<SubscriptionPlan> plans;
}