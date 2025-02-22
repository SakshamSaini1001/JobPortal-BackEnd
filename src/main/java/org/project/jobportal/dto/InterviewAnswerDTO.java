package org.project.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.jobportal.model.InterviewAnswer;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewAnswerDTO {
    @Id
    private String id;
    private String mockIdRef;
    private String question;
    private String correctAnswer;
    private String userAnswer;
    private String feedback;
    private String rating;
    private Long createdBy;
    private LocalDateTime createdAt;

    public InterviewAnswer toEntity(){
        return new InterviewAnswer(
                this.id,this.mockIdRef,this.question,this.correctAnswer,
                this.userAnswer,this.feedback,this.rating,
                this.createdBy,this.createdAt
        );
    }
}
