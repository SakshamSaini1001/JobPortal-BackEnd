package org.project.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Certifications {
    private String name;
    private String issuer;
    private LocalDateTime issueDate;
    private String certificateId;
}

