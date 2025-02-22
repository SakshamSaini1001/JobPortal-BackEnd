package org.project.jobportal.controller;

import org.project.jobportal.dto.InterviewAnswerDTO;
import org.project.jobportal.dto.MockInterviewDTO;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.service.MockInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mockInterview")
public class MockInterviewController {

    @Value("${gemini.api.key}") // ✅ Ensure API Key is correctly set in application.properties
    private String apiKey;

    @Autowired
        private MockInterviewService mockInterviewService;

    @GetMapping("/createdBy/{userId}")
    public List<MockInterviewDTO> getMockInterviewsByUser(@PathVariable Long userId) {
        return mockInterviewService.getMockInterviewsByCreatedBy(userId);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MockInterviewDTO> getMockInterivew(@PathVariable String id) throws JobPortalException {
        return new ResponseEntity<>(mockInterviewService.getMockInterview(id), HttpStatus.OK);
    }

    @GetMapping("/get/InterviewAnswer/{id}")
    public ResponseEntity<InterviewAnswerDTO> getInterivewAnswer(@PathVariable String id) throws JobPortalException {
        return new ResponseEntity<>(mockInterviewService.getInterviewAnswer(id), HttpStatus.OK);
    }

    @GetMapping("/{mockIdRef}")
    public List<InterviewAnswerDTO> getInterviewAnswers(@PathVariable String mockIdRef) {
        return mockInterviewService.getAnswersByMockRefId(mockIdRef);
    }

    @PostMapping("/save")
    public ResponseEntity<MockInterviewDTO> saveMockInterview(@RequestBody MockInterviewDTO mockInterviewDTO) throws JobPortalException {
        MockInterviewDTO savedMockInterviewDTO = mockInterviewService.saveMockInterview(mockInterviewDTO);
        return ResponseEntity.ok(savedMockInterviewDTO);
    }

    @PostMapping("/save/InterviewAnswer")
    public ResponseEntity<InterviewAnswerDTO> saveInterviewAnswer(@RequestBody InterviewAnswerDTO interviewAnswerDTO) throws JobPortalException {
        InterviewAnswerDTO savedInterviewAnswerDTO = mockInterviewService.saveInterviewAnswer(interviewAnswerDTO);
        return ResponseEntity.ok(savedInterviewAnswerDTO);
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> getFeedback(@RequestBody Map<String, String> request) {
        try {
            // Extract question and userAnswer from request body
            String question = request.get("question");
            String userAnswer = request.get("userAnswer");

            if (question == null || userAnswer == null) {
                return ResponseEntity.badRequest().body("Error: Question and User Answer are required.");
            }

            // Call service method to get feedback
            String feedback = mockInterviewService.getFeedback(question, userAnswer);

            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Unable to generate feedback.");
        }
    }

    @GetMapping("/generate")
        public ResponseEntity<?> generateInterviewQuestions(
                @RequestParam String jobPosition,
                @RequestParam String jobDescription,
                @RequestParam int yearsOfExperience ,
                @RequestHeader("Authorization") String authorizationHeader,
                @RequestHeader(value = "x-api-key", required = false) String apiKey) {

            // 🔹 Check if JWT is present
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid JWT token");
            }

            // 🔹 Extract JWT Token
            String token = authorizationHeader.substring(7);
             try {
            if (apiKey == null || apiKey.isEmpty()) {
                throw new RuntimeException("Gemini API Key is missing!");
            }

            // ✅ Call the Gemini API
                 String response = mockInterviewService.generateInterviewQuestions(jobPosition, jobDescription, yearsOfExperience);

                 System.out.println("Raw Gemini API Response: " + response);

                 return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // ✅ Print stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
        }
    }

