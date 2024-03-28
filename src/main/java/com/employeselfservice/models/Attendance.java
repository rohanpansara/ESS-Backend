package com.employeselfservice.models;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public enum AttendanceStatus{
        PR,AB
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "a_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "e_id")
    private Employee employee;

    @Column(name = "a_date", nullable = false)
    private LocalDate date;

    @Column(name = "a_work_hours")
    private String workHours;

    @Column(name = "a_first_punch")
    private LocalDateTime firstPunchIn;

    @Column(name = "a_last_punch")
    private LocalDateTime lastPunchOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "a_first_half")
    private AttendanceStatus firstHalf;

    @Enumerated(EnumType.STRING)
    @Column(name = "a_second_half")
    private AttendanceStatus secondHalf;

    @Column(name = "a_final_punch_out_time")
    private String canLeaveByTime;

    public void calculateWorkHours(List<PunchIn> punchIns, List<PunchOut> punchOuts) {
        Duration totalWorkHours = Duration.ZERO;

        int punchOutsSize = punchOuts.size();
        for (int i = 0; i < punchIns.size(); i++) {
            LocalDateTime punchInTime = punchIns.get(i).getPunchInTime();
            LocalDateTime punchOutTime;

            System.out.println(punchIns);
            System.out.println(punchOuts);

            // if the employee haven't officially punched out yet
            if (i < punchOutsSize) {
                punchOutTime = punchOuts.get(i).getPunchOutTime();
                this.setFirstPunchIn(punchIns.get(0).getPunchInTime());
                this.setLastPunchOut(punchOuts.get(punchOuts.size()-1).getPunchOutTime());
            } else {
                punchOutTime = LocalDateTime.now();
                this.setFirstPunchIn(punchIns.get(0).getPunchInTime());
                this.setLastPunchOut(null);
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
        this.canLeaveByTime = finalPunchOutDateTime.format(formatter);

        // Update firstHalf and secondHalf status
        if (hours >= 3 && hours <= 7) {
            this.firstHalf = AttendanceStatus.PR;
            this.secondHalf = AttendanceStatus.AB;
        } else if (hours >= 7 && minutes>=30) {
            this.firstHalf = AttendanceStatus.PR;
            this.secondHalf = AttendanceStatus.PR;
        } else {
            this.firstHalf = AttendanceStatus.AB;
            this.secondHalf = AttendanceStatus.AB;
        }
    }
}
