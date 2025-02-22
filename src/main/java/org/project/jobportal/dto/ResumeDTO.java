package org.project.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobportal.model.Applicant;
import org.project.jobportal.model.Resume;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {

    @Id
    private String id;
    private Long resumeId;
    private String userId;
    private String fileName;
    private String resume;
    private String extractedText;
    private String rating;
    private String feedback;
    private String areaOfImprovement;
    private LocalDateTime uploadDate;

    public Resume toEntity() {
        return new Resume(
                this.id,
                this.resumeId,
                this.userId,
                this.fileName,
                this.resume!=null? Base64.getDecoder().decode(this.resume) :null,
                this.extractedText,
                this.rating,
                this.feedback,
                this.areaOfImprovement,
                this.uploadDate
        );
    }
}

