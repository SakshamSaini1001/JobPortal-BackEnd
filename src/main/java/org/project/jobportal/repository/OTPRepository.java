package org.project.jobportal.repository;

import org.project.jobportal.model.OTP;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OTPRepository extends MongoRepository<OTP, String> {
List<OTP> findByCreationTimeBefore(LocalDateTime expiry);
}
