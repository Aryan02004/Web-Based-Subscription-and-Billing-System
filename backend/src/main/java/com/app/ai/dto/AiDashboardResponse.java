package com.app.ai.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiDashboardResponse {

//	private String organization;
//
//	private String businessHealth;
//
//	private Integer healthScore;
//
//	private String executiveSummary;
//
//	private List<String> keyInsights;
//
//	private List<String> recommendations;
//
//	private LocalDateTime generatedAt;
	private String organization;
	private String aiReport;
	private LocalDateTime generatedAt;

}