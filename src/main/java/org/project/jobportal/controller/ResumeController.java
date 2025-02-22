package org.project.jobportal.controller;

//import org.project.jobportal.model.Resume;
//import org.project.jobportal.service.ResumeAnalysisService;
//import org.project.jobportal.service.ResumeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Map;
//
//@RestController
//@CrossOrigin
//@Validated
//@RequestMapping("/resume")
//public class ResumeController {
//
//    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);
//
//    @Autowired
//    private ResumeService resumeService;
//
//    @Autowired
//    private ResumeAnalysisService resumeAnalysisService;
//
//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file,
//                                               @RequestParam("userId") String userId) {
//        try {
//            // Log file and userId details
//            logger.info("Received file: {}", file.getOriginalFilename());
//            logger.info("Received userId: {}", userId);
//
//            if (file.isEmpty()) {
//                logger.warn("File is empty!");
//                return ResponseEntity.badRequest().body("File is empty!");
//            }
//
//            if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
//                logger.warn("File size exceeds 5MB limit.");
//                return ResponseEntity.badRequest().body("File size exceeds 5MB limit.");
//            }
//
//            String extractedText = resumeService.saveResume(file, userId);
//            if (extractedText == null || extractedText.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Failed to extract text from resume.");
//            }
//            return ResponseEntity.ok(Map.of("extractedText", extractedText));
//        } catch (Exception e) {
//            logger.error("Error processing file", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error processing file: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/analyze")
//    public ResponseEntity<?> analyzeResume(@RequestBody Map<String, String> requestBody) {
//        String text = requestBody.get("text");
//
//        if (text == null || text.isEmpty()) {
//            return ResponseEntity.badRequest().body("No text provided for analysis");
//        }
//
//        try {
//            String feedback = resumeAnalysisService.analyzeResume(text);
//            return ResponseEntity.ok(Map.of("feedback",feedback));
//        } catch (Exception e) {
//            logger.error("Resume analysis failed", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error analyzing resume: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/latest")
//    public ResponseEntity<?> getLatestResume(@RequestParam("userId") String userId) {
//        try {
//            logger.info("Fetching latest resume for userId: {}", userId);
//
//            Resume latestResume = resumeService.getLatestResumeByUserId(userId);
//            if (latestResume == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No resume found for userId: " + userId);
//            }
//
//            return ResponseEntity.ok(latestResume);
//        } catch (Exception e) {
//            logger.error("Error fetching resume", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error retrieving resume: " + e.getMessage());
//        }
//    }

//}


import org.project.jobportal.dto.MockInterviewDTO;
import org.project.jobportal.dto.ResumeDTO;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.model.Resume;
import org.project.jobportal.service.ResumeAnalysisService;
import org.project.jobportal.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/resume")
public class ResumeController {

    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ResumeAnalysisService resumeAnalysisService;

    /**
     * ✅ Upload Resume, Extract Text & Analyze it Automatically
     */
    @PostMapping("/upload")
    public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file) {
        try {
            String extractedText = resumeService.extractText(file);
            return ResponseEntity.ok().body(extractedText);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error extracting text: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<ResumeDTO> saveResume(@RequestBody ResumeDTO resumeDTO) throws JobPortalException {
        ResumeDTO savedResumeDTO = resumeService.saveResume(resumeDTO);
        return ResponseEntity.ok(savedResumeDTO);
    }

    /**
     * ✅ Analyze Resume Text with Google AI
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(@RequestBody Map<String, String> requestBody) {
        try {
            String text = requestBody.get("text");

            if (text == null || text.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No text provided for analysis"));
            }

            String feedback = resumeAnalysisService.resumeAnalysis(text);
            return ResponseEntity.ok(Map.of("feedback", feedback));
        } catch (Exception e) {
            logger.error("Resume analysis failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error analyzing resume: " + e.getMessage()));
        }
    }

    /**
     * ✅ Get Latest Resume of User
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestResume(@RequestParam("userId") String userId) {
        try {
            logger.info("Fetching latest resume for userId: {}", userId);

            ResumeDTO latestResume = resumeService.getLatestResumeByUserId(userId);
            if (latestResume == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No resume found for userId: " + userId));
            }

            return ResponseEntity.ok(Map.of(
                    "resumeId", latestResume.getId(),
                    "extractedText", latestResume.getExtractedText()
            ));
        } catch (Exception e) {
            logger.error("Error fetching resume", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving resume: " + e.getMessage()));
        }
    }

}

