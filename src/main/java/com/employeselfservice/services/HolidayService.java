package com.employeselfservice.services;

import com.employeselfservice.models.Holiday;
import com.employeselfservice.repositories.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

@Service
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    public List<Holiday> findAllHolidays() throws SQLSyntaxErrorException {
        return holidayRepository.findAll();
    }
}
