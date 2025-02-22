package org.project.jobportal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobportal.dto.MockInterviewDTO;
import org.project.jobportal.dto.QuestionAnswer;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockInterview {
    @Id  // This will be MongoDB's unique identifier (String)
    private String id;
    private Long mockId;
    private List<QuestionAnswer> response;
    private String jobPosition;
    private String jobDesc;
    private String jobExperience;
    private Long createdBy;
    private LocalDateTime createdAt;

    public MockInterviewDTO toDTO(){
        return new MockInterviewDTO(
                this.id,this.mockId,this.response,this.jobPosition,this.jobDesc,
                this.jobExperience,this.createdBy,this.createdAt
        );
    }
}
