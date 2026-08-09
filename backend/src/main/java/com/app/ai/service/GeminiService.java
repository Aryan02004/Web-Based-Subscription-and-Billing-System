

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

    @Value("#{'${gemini.models}'.split(',')}")
    private String[] models;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public String generateContent(String prompt) {

        Map<String, Object> body = Map.of(
                "contents",
                new Object[]{
                        Map.of(
                                "parts",
                                new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                });

        try {

            String json = objectMapper.writeValueAsString(body);

            for (String model : models) {

                String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                        + model.trim()
                        + ":generateContent?key="
                        + apiKey;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {

                    JsonNode root = objectMapper.readTree(response.body());

                    return root.path("candidates")
                            .get(0)
                            .path("content")
                            .path("parts")
                            .get(0)
                            .path("text")
                            .asText();
                }

                System.out.println("Model " + model +
                        " failed. Status = " + response.statusCode());
            }

            throw new RuntimeException("All Gemini models failed.");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }
}