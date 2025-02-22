package org.project.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobportal.model.MockInterview;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MockInterviewDTO {
    @Id  // This will be MongoDB's unique identifier (String)
    private String id;
    private Long mockId;
    private List<QuestionAnswer> response;
    private String jobPosition;
    private String jobDesc;
    private String jobExperience;
    private Long createdBy;
    private LocalDateTime createdAt;

    public MockInterview toEntity(){
        return new MockInterview(
                this.id,this.mockId,this.response,this.jobPosition,this.jobDesc,
                this.jobExperience,this.createdBy,this.createdAt
        );
    }
}
