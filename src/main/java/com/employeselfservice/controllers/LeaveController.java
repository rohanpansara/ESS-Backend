package com.employeselfservice.controllers;

import com.employeselfservice.dao.request.LeaveRequest;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Leave;
import com.employeselfservice.models.Team;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.LeaveService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class LeaveController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/user/leaves/getAllLeave")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllLeavesForUser(@RequestParam Long id) {
        try {
            if(id==null){
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Employee ID not found");
            }
            List<Leave> leaves = leaveService.findAllLeavesForEmployee(id);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Leave Applications Fetched");
            apiResponse.setData(leaves);
            System.out.println("Leave Applications Fetched!");
            return ResponseEntity.ok(apiResponse);
        } catch (EmptyResultDataAccessException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("No Leave Applications Found From You");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }catch (AccessDeniedException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Either your token expired or You are not logged in: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
        } catch (DataAccessException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Database access error occurred while fetching leave applications: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PostMapping("/user/leaves/applyLeave")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> applyForLeave(@RequestBody LeaveRequest leaveRequest){
        try {
            LocalDate today = LocalDate.now();
            LocalDate leaveFrom = leaveRequest.getLeaveFrom();
            LocalDate leaveTo = leaveRequest.getLeaveTo();

            if (leaveFrom.isAfter(today.minusDays(1))) {
                if (leaveTo.isBefore(leaveFrom)) {
                    apiResponse.setSuccess(false);
                    apiResponse.setMessage("'Absence To' cannot be before 'Absence From'");
                    System.out.println("Absence Date Error");
                    apiResponse.setData(null);
                    return ResponseEntity.badRequest().body(apiResponse);
                } else {
                    String leaveResponse = leaveService.applyForLeave(leaveRequest);
                    if (leaveResponse.equals("Leave_Applied")) {
                        apiResponse.setSuccess(true);
                        apiResponse.setMessage("Leave Application Sent");
                        apiResponse.setData(null);
                        System.out.println("Leave Applied Successfully!");
                    }
                    else if(leaveResponse.equals("User_Not_Found")){
                        apiResponse.setSuccess(false);
                        apiResponse.setMessage("User Not Found");
                        apiResponse.setData(null);
                    }
                    else {
                        apiResponse.setSuccess(false);
                        apiResponse.setMessage("Error Applying For Leave");
                        apiResponse.setData(null);
                        return ResponseEntity.badRequest().body(apiResponse);
                    }
                }
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Cannot Apply For Leave Before " + today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                return ResponseEntity.badRequest().body(apiResponse);
            }
            return ResponseEntity.ok(apiResponse);
        }catch (ExpiredJwtException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Token Expired. Login Again");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PostMapping("/user/manager/leaves/updateStatus")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> approveLeave(@RequestParam int id,@RequestParam String status){
        try {
            System.out.println("Inside Approve Leave");
            String leaveResponse = leaveService.approveLeave(id,status);
            switch (leaveResponse) {
                case "Approved" -> {
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("Leave Approved");
                    apiResponse.setData(null);
                }
                case "Rejected" -> {
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("Leave Rejected");
                    apiResponse.setData(null);
                }
                case "Leave_Not_Found" -> {
                    apiResponse.setSuccess(false);
                    apiResponse.setMessage("No Such Leave Application Found!");
                    apiResponse.setData(null);
                }
                default -> {
                    apiResponse.setSuccess(false);
                    apiResponse.setMessage("Something Went Wrong!");
                    apiResponse.setData(null);
                }
            }
            return ResponseEntity.ok(apiResponse);
        }
        catch (ExpiredJwtException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Token Expired. Login Again!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/user/manager/leaves/getAllLeave")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllLeavesForManager(@RequestParam Long id) {
        try{
            Team team = employeeService.checkForManager(id);
            System.out.println(team);
            if(team==null){
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Team Not Found!");
                apiResponse.setData(null);
                return ResponseEntity.badRequest().body(apiResponse);
            }
            else{
                List<Leave> leaves = leaveService.findAllApprovedLeavesByTeam(team);
                System.out.println(leaves);
                if(!leaves.isEmpty()){
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("All leaves fetched for Team-"+team.getName());
                    apiResponse.setData(leaves);
                }
                else{
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("No pending leave applications for the team");
                    apiResponse.setData(leaves);
                }
                return ResponseEntity.ok(apiResponse);
            }
        }
        catch (ExpiredJwtException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Token Expired. Login Again!");
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        } catch (AccessDeniedException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Either your token expired or You are not logged in: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
        } catch (DataAccessException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Database access error occurred while fetching leave applications: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Server Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
