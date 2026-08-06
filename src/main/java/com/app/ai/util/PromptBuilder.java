package com.app.ai.util;

import com.app.ai.dto.DashboardMetrics;

public class PromptBuilder {

	public static String buildPrompt(DashboardMetrics m) {

		return """
				You are an experienced SaaS Business Consultant.

				Analyze this organization's subscription business.

				Organization : %s

				Customers : %d

				Subscriptions : %d

				Plans : %d

				Revenue : ₹%.2f

				Renewals Due : %d

				Top Plan : %s

				Give your response in exactly this format.

				Business Health:
				<Excellent/Good/Average/Poor>

				Health Score:
				<number between 0 and 100>

				Executive Summary:
				<2-3 lines>

				Key Insights:
				- insight 1
				- insight 2
				- insight 3

				Recommendations:
				- recommendation 1
				- recommendation 2
				- recommendation 3

				Keep response under 180 words.
				""".formatted(m.getOrganizationName(), m.getTotalCustomers(), m.getTotalSubscriptions(),
				m.getTotalPlans(), m.getTotalRevenue(), m.getRenewalsDue(), m.getTopPlan());

	}

}