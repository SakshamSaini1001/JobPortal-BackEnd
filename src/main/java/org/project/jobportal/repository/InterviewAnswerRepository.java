package org.project.jobportal.repository;

import org.project.jobportal.dto.InterviewAnswerDTO;
import org.project.jobportal.model.InterviewAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewAnswerRepository extends MongoRepository<InterviewAnswer,String> {

    List<InterviewAnswerDTO> findByMockIdRefOrderByIdAsc(String mockIdRef);
}
