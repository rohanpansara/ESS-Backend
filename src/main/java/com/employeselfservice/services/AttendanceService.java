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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private PunchInRepository punchInRepository;

    @Autowired
    private PunchOutRepository punchOutRepository;

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
        // Fetch PunchIn and PunchOut records for the employee and date from repositories
        List<PunchIn> punchIns = punchInRepository.findByEmployeeIdAndDate(employeeId, date);
        List<PunchOut> punchOuts = punchOutRepository.findByEmployeeIdAndDate(employeeId, date);

        // Create an instance of Attendance entity
        Attendance attendance = new Attendance();
        attendance.setEmployee(new Employee(employeeId));
        attendance.setDate(date);

        // Calculate work hours and set in attendance entity
        attendance.calculateWorkHours(punchIns, punchOuts);

        // Save the new attendance record
        return attendanceRepository.save(attendance);
    }
}
