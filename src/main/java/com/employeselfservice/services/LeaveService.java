package com.employeselfservice.services;

import com.employeselfservice.dao.request.LeaveRequest;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Leave;
import com.employeselfservice.models.Notifications;
import com.employeselfservice.models.Team;
import com.employeselfservice.repositories.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private NotificationService notificationService;

    public List<Leave> findAllLeavesForEmployee(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }

    public String applyForLeave(LeaveRequest leaveRequest) {
        Employee employee = employeeService.findEmployeeById(leaveRequest.getEmployeeId());
        if (employee == null) {
            return "User_Not_Found";
        }

        Leave leave = new Leave();
        leave.setEmployee(employee);
        leave.setReason(leaveRequest.getLeaveReason());
        leave.setAppliedOn(LocalDate.now());
        leave.setOverflow(0); // Set overflow to 0 when applying for leave

        // Set leave days based on leave type
        if (leaveRequest.getLeaveFrom().isEqual(leaveRequest.getLeaveTo())) {
            leave.setDays(leaveRequest.getLeaveCount());
        } else {
            long daysBetween = ChronoUnit.DAYS.between(leaveRequest.getLeaveFrom(), leaveRequest.getLeaveTo()) + 1;
            leave.setDays(daysBetween);
        }

        leave.setFrom(leaveRequest.getLeaveFrom());
        leave.setTo(leaveRequest.getLeaveTo());
        leave.setStatus(Leave.LeaveStatus.PENDING);
        leave.setType(leaveRequest.getLeaveType());
        leave.setMonth(leaveRequest.getLeaveFrom().getMonthValue());

        leaveRepository.save(leave);
        return "Leave_Applied";
    }

    public String approveLeave(int id, String status){
        Long leaveId = (long) id;
        Leave leave = leaveRepository.findById(leaveId).get();


        if(leave.getDays()>=1.5){
            leave.setOverflow(leave.getDays()-1.5);
        }
        if(leave==null){
            return "Leave_Not_Found";
        }
        if(status.equals("APPROVED")){
            double overflow = leaveRepository.getOverflowForLatestApprovedLeaveInMonth(leave.getEmployee().getId(), leave.getMonth());
            double newOverflow = overflow;
            if (leave.getDays() >= 1.5 && newOverflow>=0) {
                newOverflow += leave.getDays() - 1.5; // Add difference if the leave itself exceeds 1.5 days
            }
            if (newOverflow >= 1.5) {
                leave.setOverflow(newOverflow);
            } else {
                leave.setOverflow(leave.getDays() - 1.5); // Reset overflow
            }
            leave.setStatus(Leave.LeaveStatus.APPROVED);
            leaveRepository.save(leave);


            Notifications notification = new Notifications();
            notification.setEmployee(leave.getEmployee());
            notification.setNotification("Your Application For "+leave.getReason()+" Was Approved!");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setType(Notifications.NotificationType.UPDATES);

            String notificationResponse = notificationService.addNotificationForUser(notification);
            System.out.println("Notification For Leave Approval - "+notificationResponse.toUpperCase());


            return "Approved";
        }
        else if(status.equals("REJECTED")){
            leave.setStatus(Leave.LeaveStatus.REJECTED);
            leave.setOverflow(0);


            Notifications notification = new Notifications();
            notification.setEmployee(leave.getEmployee());
            notification.setNotification("Your Application For "+leave.getReason()+" Was Rejected!");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setType(Notifications.NotificationType.UPDATES);

            String notificationResponse = notificationService.addNotificationForUser(notification);
            System.out.println("Notification For Leave Rejection - "+notificationResponse.toUpperCase());


            return "Rejected";
        }
        return "Error";
    }

    public List<Leave> findAllApprovedLeavesByTeam(Team team){
        return leaveRepository.findAllApprovedLeavesByTeam(team);
    }

    public List<Leave> findAll(){
        return leaveRepository.findAll();
    }
}
