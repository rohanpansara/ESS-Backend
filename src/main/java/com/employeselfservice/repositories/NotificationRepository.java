package com.employeselfservice.repositories;

import com.employeselfservice.models.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {
    List<Notifications> findAllByEmployeeId(Long employeeId);
}
