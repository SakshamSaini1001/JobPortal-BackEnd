package org.project.jobportal.repository;

import org.project.jobportal.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<Profile, Long> {
    
}
