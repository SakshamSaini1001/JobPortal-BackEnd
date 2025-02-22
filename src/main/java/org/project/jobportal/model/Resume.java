package org.project.jobportal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobportal.dto.ResumeDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    @Id
    private String id;
    private Long resumeId;
    private String userId;
    private String fileName;
    private byte[] resume;
    private String extractedText;
    private String rating;
    private String feedback;
    private String areaOfImprovement;
    private LocalDateTime uploadDate;

    public ResumeDTO toDTO() {
        return new ResumeDTO(
                this.id,
                this.resumeId,
                this.userId,
                this.fileName,
                this.resume != null ? Base64.getEncoder().encodeToString(this.resume) : null,
                this.extractedText,
                this.rating,
                this.feedback,
                this.areaOfImprovement,
                this.uploadDate
        );
    }
}

