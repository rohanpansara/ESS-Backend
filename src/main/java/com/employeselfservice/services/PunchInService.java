package com.employeselfservice.services;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.PunchIn;
import com.employeselfservice.models.PunchOut;
import com.employeselfservice.repositories.PunchInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PunchInService {
    @Autowired
    private PunchInRepository punchInRepository;

    public String addPunchIn(Long id){
        Employee employee = new Employee(id);
        punchInRepository.save(new PunchIn(employee));
        return "punched";
    }

    public List<PunchIn> getAllPunchInsForEmployeeForToday(Employee employee){
        return punchInRepository.findByEmployeeIdAndDate(employee.getId(), LocalDate.now());
    }
}
