package com.employeselfservice.services;

import com.employeselfservice.models.Team;
import com.employeselfservice.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public Optional<Team> findById(Long id){
        return teamRepository.findById(id);
    }
}
