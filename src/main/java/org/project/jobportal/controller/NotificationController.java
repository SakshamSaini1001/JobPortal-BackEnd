package org.project.jobportal.controller;

import org.project.jobportal.dto.ResponseDTO;
import org.project.jobportal.exception.JobPortalException;
import org.project.jobportal.model.Notification;
import org.project.jobportal.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/notification")
@Validated
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/get/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        return new ResponseEntity<>(notificationService.getUnreadNotifications(userId), HttpStatus.OK);
    }

    @PutMapping("/read/{id}")
    public ResponseEntity<ResponseDTO> readNotification(@PathVariable Long id) throws JobPortalException {
        notificationService.readNotification(id);
        return new ResponseEntity<>(new ResponseDTO("Success"),HttpStatus.OK);
    }
}
