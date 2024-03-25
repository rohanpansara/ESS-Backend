package com.employeselfservice.controllers;

import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Holiday;
import com.employeselfservice.services.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class HolidayController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private HolidayService holidayService;

    @GetMapping("/holiday/getAll")
    public ResponseEntity<ApiResponse> getAllHolidays(){
        try{
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Holidays Fetched");
            apiResponse.setData(holidayService.findAllHolidays());
            return ResponseEntity.ok(apiResponse);
        } catch (SQLSyntaxErrorException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Database Error: "+e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}
