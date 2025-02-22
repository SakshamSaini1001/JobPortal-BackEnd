package org.project.jobportal.repository;

import org.project.jobportal.dto.MockInterviewDTO;
import org.project.jobportal.model.MockInterview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MockInterviewRepository extends MongoRepository<MockInterview, String> {
    // You can add custom query methods here if
    List<MockInterviewDTO> findByCreatedByOrderByIdDesc(Long createdBy);
    public Optional<MockInterview> findMockInterviewByMockId(Long mockId);
}
