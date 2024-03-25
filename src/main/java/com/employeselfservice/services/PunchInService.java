package com.employeselfservice.services;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.PunchIn;
import com.employeselfservice.models.PunchOut;
import com.employeselfservice.repositories.PunchInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PunchInService {
    @Autowired
    private PunchInRepository punchInRepository;

    public String addPunchIn(Long id){
        Employee employee = new Employee(id);
        punchInRepository.save(new PunchIn(employee));
        return "punched";
    }

}
