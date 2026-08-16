package com.example.complaintmanagement.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AIComplaintAnalyzerService implements ComplaintAIService {

    private static final Logger LOGGER = Logger.getLogger(AIComplaintAnalyzerService.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.model}")
    private String model;

    @Value("${ai.endpoint}")
    private String endpoint;

    @Override
    public AIResponse analyzeComplaint(String title, String description) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            LOGGER.info("Gemini API key is not configured. Falling back to deterministic analysis.");
            return runDeterministicFallback(title, description);
        }

        try {
            // Build Gemini request URL
            // Format: https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=YOUR_API_KEY
            String targetUrl = endpoint + "/" + model + ":generateContent?key=" + apiKey;
            
            // Build Prompt
            String prompt = String.format(
                "You are an AI assistant analyzing maintenance and service complaints. " +
                "Analyze the following complaint and return a JSON object with fields: " +
                "\"category\" (must be one of: NETWORK, ELECTRICITY, PLUMBING, CLEANING, MAINTENANCE, COMPUTER, SECURITY, OTHER), " +
                "\"priority\" (must be one of: LOW, MEDIUM, HIGH, CRITICAL), " +
                "\"department\" (a short department name like: IT Support, Electrical Department, Plumbing Department, Housekeeping & Cleaning, General Maintenance, Security Department), " +
                "\"summary\" (a short 1-sentence summary), " +
                "\"suggestedResponse\" (a suggested action for staff or response to the user). " +
                "Do not return markdown, do not wrap in ```json, return ONLY the raw JSON object.\n\n" +
                "Complaint Title: %s\n" +
                "Complaint Description: %s",
                title, description
            );

            // Escape string for JSON
            String jsonPrompt = objectMapper.writeValueAsString(prompt);

            String requestBody = String.format(
                "{\"contents\": [{\"parts\": [{\"text\": %s}]}]}",
                jsonPrompt
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(7))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                LOGGER.fine("Gemini API Response: " + responseBody);
                return parseGeminiResponse(responseBody);
            } else {
                LOGGER.warning("Gemini API returned error code: " + response.statusCode() + " Body: " + response.body());
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occurred during Gemini AI analysis", e);
        }

        LOGGER.info("AI Analysis failed or timed out. Running deterministic fallback.");
        return runDeterministicFallback(title, description);
    }

    private AIResponse parseGeminiResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                String textResponse = candidates.get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text")
                        .asText()
                        .trim();

                // Clean potential markdown wrap
                if (textResponse.startsWith("```json")) {
                    textResponse = textResponse.substring(7);
                }
                if (textResponse.endsWith("```")) {
                    textResponse = textResponse.substring(0, textResponse.length() - 3);
                }
                textResponse = textResponse.trim();

                JsonNode parsedResponse = objectMapper.readTree(textResponse);
                return AIResponse.builder()
                        .category(parsedResponse.path("category").asText("OTHER").toUpperCase())
                        .priority(parsedResponse.path("priority").asText("MEDIUM").toUpperCase())
                        .department(parsedResponse.path("department").asText("General Maintenance"))
                        .summary(parsedResponse.path("summary").asText("Complaint received"))
                        .suggestedResponse(parsedResponse.path("suggestedResponse").asText("Review request details."))
                        .build();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing Gemini JSON response", e);
        }
        return null;
    }

    private AIResponse runDeterministicFallback(String title, String description) {
        String text = (title + " " + description).toLowerCase();

        String category = "OTHER";
        String department = "General Maintenance";
        String priority = "MEDIUM";
        String suggestedResponse = "General maintenance team should follow up on details.";

        // Category & Department Fallback
        if (text.contains("wifi") || text.contains("internet") || text.contains("router") || text.contains("network") || text.contains("ethernet") || text.contains("connection")) {
            category = "NETWORK";
            department = "IT Support";
            suggestedResponse = "Please dispatch an IT support engineer to inspect the network device.";
        } else if (text.contains("water") || text.contains("pipe") || text.contains("leak") || text.contains("tap") || text.contains("plumb") || text.contains("basin") || text.contains("toilet") || text.contains("sink") || text.contains("flush")) {
            category = "PLUMBING";
            department = "Plumbing Department";
            suggestedResponse = "Plumbing maintenance is recommended to check and stop any water leakage.";
        } else if (text.contains("electricity") || text.contains("power") || text.contains("current") || text.contains("light") || text.contains("bulb") || text.contains("fan") || text.contains("socket") || text.contains("switch") || text.contains("fuse") || text.contains("wire") || text.contains("shock")) {
            category = "ELECTRICITY";
            department = "Electrical Department";
            suggestedResponse = "A certified electrician should check the electrical panel or wiring.";
        } else if (text.contains("clean") || text.contains("sweeping") || text.contains("trash") || text.contains("dustbin") || text.contains("waste") || text.contains("garbage") || text.contains("dirt") || text.contains("broom") || text.contains("mopping") || text.contains("cleaning")) {
            category = "CLEANING";
            department = "Housekeeping & Cleaning";
            suggestedResponse = "Housekeeping staff notified for cleaning operations.";
        } else if (text.contains("computer") || text.contains("pc") || text.contains("laptop") || text.contains("monitor") || text.contains("keyboard") || text.contains("mouse") || text.contains("printer") || text.contains("software") || text.contains("ram") || text.contains("cpu")) {
            category = "COMPUTER";
            department = "IT Support";
            suggestedResponse = "IT team should inspect the computing device.";
        } else if (text.contains("security") || text.contains("guard") || text.contains("gate") || text.contains("theft") || text.contains("cctv") || text.contains("camera") || text.contains("card") || text.contains("badge") || text.contains("lock")) {
            category = "SECURITY";
            department = "Security Department";
            suggestedResponse = "Security personnel should review surveillance or access controls.";
        } else if (text.contains("maintain") || text.contains("furniture") || text.contains("door") || text.contains("window") || text.contains("key") || text.contains("table") || text.contains("chair") || text.contains("desk") || text.contains("bed") || text.contains("wall") || text.contains("paint")) {
            category = "MAINTENANCE";
            department = "General Maintenance";
            suggestedResponse = "General maintenance team to inspect and repair structural/furniture parts.";
        }

        // Priority Fallback
        if (text.contains("fire") || text.contains("shock") || text.contains("flood") || text.contains("short circuit") || text.contains("injury") || text.contains("danger") || text.contains("critical") || text.contains("emergency")) {
            priority = "CRITICAL";
        } else if (text.contains("urgent") || text.contains("broken") || text.contains("outage") || text.contains("leakage") || text.contains("high") || text.contains("stopped")) {
            priority = "HIGH";
        } else if (text.contains("slow") || text.contains("faulty") || text.contains("not working") || text.contains("medium")) {
            priority = "MEDIUM";
        } else {
            priority = "LOW";
        }

        // Summary Fallback
        String summary = title.length() > 50 ? title.substring(0, 47) + "..." : title;
        summary = "Auto-classified: " + summary;

        return AIResponse.builder()
                .category(category)
                .priority(priority)
                .department(department)
                .summary(summary)
                .suggestedResponse(suggestedResponse)
                .build();
    }
}
