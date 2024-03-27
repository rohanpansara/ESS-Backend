package com.employeselfservice.services;

import com.employeselfservice.models.Attendance;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.PunchIn;
import com.employeselfservice.models.PunchOut;
import com.employeselfservice.repositories.AttendanceRepository;
import com.employeselfservice.repositories.PunchInRepository;
import com.employeselfservice.repositories.PunchOutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private PunchInRepository punchInRepository;

    @Autowired
    private PunchInService punchInService;

    @Autowired
    private PunchOutRepository punchOutRepository;

    @Autowired
    private PunchOutService punchOutService;

    public Attendance calculateAttendance(Long employeeId, LocalDate date) {
        // Check if an attendance record already exists for the given employee and date
        Optional<Attendance> existingAttendanceOptional = attendanceRepository.findByEmployeeIdAndDate(employeeId, date);
        if (existingAttendanceOptional.isPresent()) {
            // If an attendance record already exists, update it with new work hours
            Attendance existingAttendance = existingAttendanceOptional.get();
            updateWorkHours(existingAttendance, employeeId, date);
            return attendanceRepository.save(existingAttendance);
        } else {
            // If no attendance record exists, create a new one
            return createNewAttendance(employeeId, date);
        }
    }

    private void updateWorkHours(Attendance attendance, Long employeeId, LocalDate date) {
        // Fetch PunchIn and PunchOut records for the employee and date from repositories
        List<PunchIn> punchIns = punchInRepository.findByEmployeeIdAndDate(employeeId, date);
        List<PunchOut> punchOuts = punchOutRepository.findByEmployeeIdAndDate(employeeId, date);

        // Calculate work hours and set in attendance entity
        attendance.calculateWorkHours(punchIns, punchOuts);
    }

    private Attendance createNewAttendance(Long employeeId, LocalDate date) {
        // Fetch PunchIn and PunchOut records for the employee and date
        List<PunchIn> punchIns = punchInRepository.findByEmployeeIdAndDate(employeeId, date);
        List<PunchOut> punchOuts = punchOutRepository.findByEmployeeIdAndDate(employeeId, date);

        Attendance attendance = new Attendance();
        attendance.setEmployee(new Employee(employeeId));
        attendance.setDate(date);

        // Calculate work hours and set in attendance entity
        attendance.calculateWorkHours(punchIns, punchOuts);

        // Save the new attendance record
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceForEmployee(Long employeeId) {
        List<Attendance> allAttendances = attendanceRepository.findByEmployeeId(employeeId);
        List<Attendance> filteredAttendances = new ArrayList<>();

        for (Attendance attendance : allAttendances) {
            String workHoursString = attendance.getWorkHours();
            if (workHoursString != null) {
                // Split workHoursString into hours and minutes
                String[] parts = workHoursString.split(":");
                if (parts.length == 2) {
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1].substring(0, 2)); // Removing 'pm' or 'am'

                    // Calculate total duration in minutes
                    long totalMinutes = hours * 60 + minutes;

                    // Convert total duration to Duration object
                    Duration duration = Duration.ofMinutes(totalMinutes);

                    // Compare with 5 hours and 30 minutes
                    if (duration.compareTo(Duration.ofHours(4).plusMinutes(30)) > 0) {
                        // If duration is more than 5 hours and 30 minutes, add attendance to filtered list
                        filteredAttendances.add(attendance);
                    }
                }
            }
        }

        return filteredAttendances;
    }

    public Map<String,Object> getAttendanceMaterial(Employee employee){
        Map<String, Object> attendanceMaterial = new HashMap<>();

        attendanceMaterial.put("PunchIns",punchInService.getAllPunchInsForEmployeeForToday(employee));
        attendanceMaterial.put("PunchOuts", punchOutService.getAllPunchOutsForEmployeeForToday(employee));
        attendanceMaterial.put("FirstPunchIn", punchInRepository.findFirstByEmployeeOrderByPunchInTimeAsc(employee).getPunchInTime());
        attendanceMaterial.put("LastPunchOut", punchOutRepository.findFirstByEmployeeOrderByPunchOutTimeDesc(employee).getPunchOutTime());
        attendanceMaterial.put("WorkHours", attendanceRepository.findByEmployeeId(employee.getId()));
        return attendanceMaterial;
    }
}
