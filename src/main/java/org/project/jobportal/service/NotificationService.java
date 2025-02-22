package org.project.jobportal.service;

import org.project.jobportal.dto.NotificationDTO;
import org.project.jobportal.dto.NotificationStatus;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.model.Notification;
import org.project.jobportal.repository.NotificationRepo;
import org.project.jobportal.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("NotificationService")
public class NotificationService {
    @Autowired
    private NotificationRepo notificationRepo;

    public void sendNotification(NotificationDTO notificationDTO) throws JobPortalException {
        notificationDTO.setId(Utilities.getNextSequence("notification"));
        notificationDTO.setStatus(NotificationStatus.UNREAD);
        notificationDTO.setTimestamp(LocalDateTime.now());
        notificationRepo.save(notificationDTO.toEntity());
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepo.findByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    public void readNotification(Long id) throws JobPortalException{
        Notification noti = notificationRepo.findById(id).orElseThrow(()->new JobPortalException("No Notification Found"));
        noti.setStatus(NotificationStatus.READ);
        notificationRepo.save(noti);
    }
}
