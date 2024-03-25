package com.employeselfservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "a_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "e_id", nullable = false)
    @JsonBackReference(value = "employee")
    private Employee employee;

    @Column(name = "a_date", nullable = false)
    private LocalDate date;

    @Column(name = "a_work_hours")
    private String workHours;

    @Column(name = "a_final_punch_out_time")
    private String finalPunchOutTime;

    public void calculateWorkHours(List<PunchIn> punchIns, List<PunchOut> punchOuts) {
        Duration totalWorkHours = Duration.ZERO;

        int punchOutsSize = punchOuts.size();
        for (int i = 0; i < punchIns.size(); i++) {
            LocalDateTime punchInTime = punchIns.get(i).getPunchInTime();
            LocalDateTime punchOutTime;

            // if the employee haven't officially punched out yet
            if (i < punchOutsSize) {
                punchOutTime = punchOuts.get(i).getPunchOutTime();
            } else {
                punchOutTime = LocalDateTime.now();
            }

            Duration workDuration = Duration.between(punchInTime, punchOutTime);
            totalWorkHours = totalWorkHours.plus(workDuration);
        }

        long hours = totalWorkHours.toHours();
        long minutes = totalWorkHours.toMinutesPart();

        long diff_hour = (7 - hours);
        long diff_mins = (30 - minutes);

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime finalPunchOutDateTime = currentTime.plusHours(diff_hour).plusMinutes(diff_mins);

        this.workHours = String.format("%02d:%02d", hours, minutes);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        this.finalPunchOutTime = finalPunchOutDateTime.format(formatter);
    }
}
