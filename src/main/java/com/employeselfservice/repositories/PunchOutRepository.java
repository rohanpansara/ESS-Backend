package com.employeselfservice.repositories;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.PunchOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PunchOutRepository extends JpaRepository<PunchOut, Long> {
    @Query("SELECT p FROM PunchOut p WHERE p.employee.id = :employeeId AND DATE(p.punchOutTime) = :pDate")
    List<PunchOut> findByEmployeeIdAndDate(@Param("employeeId") Long employeeId, @Param("pDate") LocalDate date);

    PunchOut findFirstByEmployeeOrderByPunchOutTimeDesc(Employee employee);

}
