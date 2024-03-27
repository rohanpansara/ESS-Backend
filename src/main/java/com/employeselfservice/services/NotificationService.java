package com.employeselfservice.services;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Notifications;
import com.employeselfservice.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notifications> getAllNotificationsForEmployee(Long employeeId) {
        return notificationRepository.findAllByEmployeeId(employeeId);
    }

    public String addNotificationForUser(Notifications notification){
        Notifications savedNotification = notificationRepository.save(notification);
        if(savedNotification==null){
            return "error";
        }
        return "added";
    }
}
