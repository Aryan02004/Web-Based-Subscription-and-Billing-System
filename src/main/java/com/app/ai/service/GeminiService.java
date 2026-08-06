package com.app.ai.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeminiService {

	@Value("${gemini.api.key}")
	private String apiKey;

	@Value("${gemini.model}")
	private String model;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public String generateContent(String prompt) {

		try {

			String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key="
					+ apiKey;

			System.out.println("URL = " + url);
			Map<String, Object> body = Map.of("contents",
					new Object[] { Map.of("parts", new Object[] { Map.of("text", prompt) }) });

			String json = objectMapper.writeValueAsString(body);

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

			HttpResponse<String> response = HttpClient.newHttpClient().send(request,
					HttpResponse.BodyHandlers.ofString());
//			System.out.println("Status = " + response.statusCode());
//			System.out.println("Body = " + response.body());
//			System.out.println(response.statusCode());
//			System.out.println(response.body());
//			System.out.println("API Key = " + apiKey);
//			System.out.println(url);
			JsonNode root = objectMapper.readTree(response.body());

			return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
//			return response.body();// temporary change

		} catch (IOException | InterruptedException e) {

			throw new RuntimeException("Failed to call Gemini API", e);

		}
	}
}