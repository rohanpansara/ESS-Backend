package com.employeselfservice.services;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.PunchOut;
import com.employeselfservice.repositories.PunchOutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PunchOutService {

    @Autowired
    private PunchOutRepository punchOutRepository;

    public String addPunchOut(Long id){
        Employee employee = new Employee(id);
        punchOutRepository.save(new PunchOut(employee));
        return "punched";
    }

}
