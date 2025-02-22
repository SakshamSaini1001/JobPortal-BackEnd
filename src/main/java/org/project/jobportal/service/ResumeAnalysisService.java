package org.project.jobportal.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResumeAnalysisService {

    @Value("${gemini.api.key}")
    private String apiKey;
    private final String apiUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ResumeAnalysisService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
//    private static final String API_KEY = "GPT_API_KEY";
//    private OpenAiService openAiService = new OpenAiService(API_KEY);


    // Load API Key from application.properties (More secure)
    public String resumeAnalysis(String text) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ✅ Correct Request Body Format
            Map<String, Object> requestBody = new HashMap<>();

            // "contents" field is mandatory in Gemini API
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> userPrompt = new HashMap<>();
            userPrompt.put("role", "user");

            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> textPart = new HashMap<>();
            textPart.put("text",
                    "You are an expert resume analyzer. Analyze the following resume and provide a rating, " +
                    "detailed feedback, and specific areas of improvement.\n\n" +
                    "### Resume Content:\n" + text + "\n\n" +
                    "### Expected Output Format (JSON):\n" +
                    "{\n" +
                    "  \"rating\": (integer between 1-10),\n" +
                    "  \"feedback\": \"Detailed overall feedback on the resume quality, clarity, and effectiveness.\",\n" +
                    "  \"areas_of_improvement\": [\n" +
                    "    \"Specific suggestion 1\",\n" +
                    "    \"Specific suggestion 2\",\n" +
                    "    \"Specific suggestion 3\"\n" +
                    "  ]\n" +
                    "}\n\n" +
                    "Ensure the response is in **valid JSON format** with no extra text or explanations." +
                            "[{\"rating\": \"...\", \"feedback\": \"...\"}]"
            );

            parts.add(textPart);
            userPrompt.put("parts", parts);
            contents.add(userPrompt);

            requestBody.put("contents", contents);

            // ✅ Add optional generation config (not required but useful)
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 1);
            generationConfig.put("topP", 0.95);
            generationConfig.put("topK", 40);
            generationConfig.put("maxOutputTokens", 8192);

            requestBody.put("generationConfig", generationConfig);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // ✅ Parse JSON Response
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            JsonNode partsNode = responseJson.path("candidates").get(0).path("content").path("parts");
            if (partsNode.isArray() && partsNode.size() > 0) {
                return partsNode.get(0).path("text").asText();
            }

            return "No response text found";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
