package org.project.jobportal.repository;

import org.project.jobportal.dto.NotificationStatus;
import org.project.jobportal.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepo extends MongoRepository<Notification, Long> {
    public List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);
}
