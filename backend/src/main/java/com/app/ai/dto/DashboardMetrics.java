package com.app.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardMetrics {

	private String organizationName;

	private int totalCustomers;

	private int totalSubscriptions;

	private int totalPlans;

	private double totalRevenue;

	private int renewalsDue;

	private String topPlan;

}