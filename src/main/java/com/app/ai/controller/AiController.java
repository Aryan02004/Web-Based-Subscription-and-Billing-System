package com.app.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.ai.dto.AiDashboardResponse;
import com.app.ai.service.AiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

	private final AiService aiService;

	@GetMapping("/dashboard-summary")
	public AiDashboardResponse dashboardSummary() {

		return aiService.generateDashboardSummary();
	}
}