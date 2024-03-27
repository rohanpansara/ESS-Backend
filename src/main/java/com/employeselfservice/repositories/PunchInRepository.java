package com.employeselfservice.repositories;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.PunchIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PunchInRepository extends JpaRepository<PunchIn, Long> {
    @Query("SELECT p FROM PunchIn p WHERE p.employee.id = :employeeId AND DATE(p.punchInTime) = :pDate")
    List<PunchIn> findByEmployeeIdAndDate(@Param("employeeId") Long employeeId, @Param("pDate") LocalDate date);

    List<PunchIn> findByEmployeeId(Long employeeId);

    PunchIn findFirstByEmployeeOrderByPunchInTimeAsc(Employee employee);
}

