//package org.project.jobportal.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class MockInterviewService {
//
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
//
//    public MockInterviewService() {
//        this.restTemplate = new RestTemplate();
//        this.objectMapper = new ObjectMapper();
//    }
//
//    public String generateInterviewQuestions(String jobPosition, String jobDescription, int yearsOfExperience) {
//        try {
//            String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=" + apiKey;
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Content-Type", "application/json");
//
//            // Prepare Request Payload
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("model", "gemini-2.0-flash");
//
//            Map<String, Object> generationConfig = new HashMap<>();
//            generationConfig.put("temperature", 1);
//            generationConfig.put("topP", 0.95);
//            generationConfig.put("topK", 40);
//            generationConfig.put("maxOutputTokens", 8192);
//
//            requestBody.put("generationConfig", generationConfig);
//
//            Map<String, Object> prompt = new HashMap<>();
//            prompt.put("role", "user");
//            prompt.put("parts", new String[]{
//                    "Job Position: " + jobPosition + "; Job Description: " + jobDescription +
//                            "; Years of Experience: " + yearsOfExperience +
//                            "; Based on this information, provide 10 interview questions with answers in JSON format."
//            });
//
//            //requestBody.put("history", new Object[]{prompt});
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//
//            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
//
//            // Extract response text
//            JsonNode responseJson = objectMapper.readTree(response.getBody());
//            return responseJson.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error: " + e.getMessage();
//        }
//    }
//}



package org.project.jobportal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.jobportal.dto.InterviewAnswerDTO;
import org.project.jobportal.dto.MockInterviewDTO;
import org.project.jobportal.dto.ProfileDTO;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.model.InterviewAnswer;
import org.project.jobportal.model.MockInterview;
import org.project.jobportal.repository.InterviewAnswerRepository;
import org.project.jobportal.repository.MockInterviewRepository;
import org.project.jobportal.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MockInterviewService {

    @Value("${gemini.api.key}")
    private String apiKey;
    private final String apiUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MockInterviewService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Autowired
    private MockInterviewRepository mockInterviewRepository;
    @Autowired
    private InterviewAnswerRepository interviewAnswerRepository;


    public MockInterviewDTO getMockInterview(String id) throws JobPortalException {
        MockInterviewDTO mockInterviewDTO = mockInterviewRepository.findById(id).orElseThrow(()->new JobPortalException("PROFILE_NOT_FOUND")).toDTO();
        System.out.println(mockInterviewDTO);
        return mockInterviewDTO;
    }

    public List<InterviewAnswerDTO> getAnswersByMockRefId(String mockIdRef) {
        return interviewAnswerRepository.findByMockIdRefOrderByIdAsc(mockIdRef);
    }

    public List<MockInterviewDTO> getMockInterviewsByCreatedBy(Long createdBy) {
        return mockInterviewRepository.findByCreatedByOrderByIdDesc(createdBy);
    }

    public InterviewAnswerDTO getInterviewAnswer(String id) throws JobPortalException {
        InterviewAnswerDTO interviewAnswerDTO = interviewAnswerRepository.findById(id).orElseThrow(()->new JobPortalException("INTERVIEW_ANSWER_NOT_FOUND")).toDTO();
        System.out.println(interviewAnswerDTO);
        return interviewAnswerDTO;
    }

    public InterviewAnswerDTO saveInterviewAnswer(InterviewAnswerDTO interviewAnswerDTO) {
        //interviewAnswerDTO.setMockId(Utilities.getNextSequence("mockInterview"));
        InterviewAnswer interviewAnswer = interviewAnswerDTO.toEntity();

        // Save the MockInterview entity to the repository
        InterviewAnswer savedInterviewAnswer = interviewAnswerRepository.save(interviewAnswer);

        // Return the saved entity as a DTO
        return savedInterviewAnswer.toDTO();
    }

    public MockInterviewDTO saveMockInterview(MockInterviewDTO mockInterviewDTO) throws JobPortalException {
        // Convert MockInterviewDTO to MockInterview entity
        mockInterviewDTO.setMockId(Utilities.getNextSequence("mockInterview"));
        MockInterview mockInterview = mockInterviewDTO.toEntity();

        // Save the MockInterview entity to the repository
        MockInterview savedMockInterview = mockInterviewRepository.save(mockInterview);

        // Return the saved entity as a DTO
        return savedMockInterview.toDTO();
    }


    public String generateInterviewQuestions(String jobPosition, String jobDescription, int yearsOfExperience) {
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
                    "Job Position: " + jobPosition +
                            "; Job Description: " + jobDescription +
                            "; Years of Experience: " + yearsOfExperience +
                            "; Provide 5 interview questions with answers in JSON format as an array: " +
                            "[{\"question\": \"...\", \"answer\": \"...\"}]"
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

//        public String getFeedback(String question, String userAnswer) {
//            try {
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//
//                // 🔹 Construct AI Prompt
//                String prompt = "Question: " + question + "\n" +
//                        "User Answer: " + userAnswer + "\n" +
//                        "Please provide a rating (1-10) and feedback as plain text.";
//
//                // 🔹 Build Correct Request Body
//                Map<String, Object> requestBody = new HashMap<>();
//
//                List<Map<String, Object>> contents = new ArrayList<>();
//                Map<String, Object> userMessage = new HashMap<>();
//                userMessage.put("role", "user");
//
//                List<Map<String, String>> parts = new ArrayList<>();
//                Map<String, String> part = new HashMap<>();
//                part.put("text", prompt);
//                parts.add(part);
//
//                userMessage.put("parts", parts);
//                contents.add(userMessage);
//                requestBody.put("contents", contents);
//
//                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//
//                // 🔹 Call Gemini API
//                ResponseEntity<String> response = restTemplate.postForEntity(apiUrl + "?key=" + apiKey, entity, String.class);
//
//                // 🔹 Parse Response Correctly
//                JsonNode responseJson = objectMapper.readTree(response.getBody());
//                JsonNode textNode = responseJson.path("candidates").get(0).path("content").path("parts").get(0).path("text");
//
//                // 🔹 Return response as plain text
//                return textNode != null ? textNode.asText() : "Error: No feedback received.";
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "Error: Failed to get feedback from Gemini AI.";
//            }
//        }

    public String getFeedback(String question, String userAnswer) {
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
                    "Question: " + question + "\n" +
                        "User Answer: " + userAnswer + "\n" +
                        "Please provide a rating (1-10) and feedback in JSON format as an array." +
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

