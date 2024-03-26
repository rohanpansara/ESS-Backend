package com.employeselfservice.controllers;


import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Attendance;
import com.employeselfservice.services.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ApiResponse apiResponse;

    @GetMapping("/user/getAllAttendance")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllAttendance(@RequestParam long id) {
        try{
            List<Attendance> attendanceList = attendanceService.getAttendanceForEmployee(id);
            if(attendanceList.isEmpty()){
                apiResponse.setSuccess(true);
                apiResponse.setMessage("No Attendance Available For User");
                apiResponse.setData(attendanceList);
            }
            else{
                apiResponse.setSuccess(true);
                apiResponse.setMessage("All Attendance Fetched");
                apiResponse.setData(attendanceList);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (NumberFormatException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Invalid employee ID format");
            return ResponseEntity.badRequest().body(apiResponse);
        } catch (NoSuchElementException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Employee not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
