package org.project.jobportal.repository;

import org.project.jobportal.dto.ResumeDTO;
import org.project.jobportal.model.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResumeRepository extends MongoRepository<Resume, String> {
    ResumeDTO findFirstByUserIdOrderByIdDesc(String userId);
}
