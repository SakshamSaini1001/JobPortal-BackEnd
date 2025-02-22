package org.project.jobportal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobportal.dto.ApplicantDTO;
import org.project.jobportal.dto.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Applicant {
    private Long applicantId;
    private String name;
    private String email;
    private Long phone;
    private String website;
    private byte[] resume;
    private String coverLetter;
    private LocalDateTime timestamp;
    private ApplicationStatus applicationStatus;
    private LocalDateTime interviewTime;

    // Corrected to return an ApplicantDTO
    public ApplicantDTO toDTO() {
        return new ApplicantDTO(
                this.applicantId,
                this.name,
                this.email,
                this.phone,
                this.website,
                this.resume != null ? Base64.getEncoder().encodeToString(this.resume) : null,
                this.coverLetter,
                this.timestamp,
                this.applicationStatus,
                this.interviewTime
        );
    }
}
