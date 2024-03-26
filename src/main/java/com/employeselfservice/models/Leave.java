package com.employeselfservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "`leave`")
public class Leave {

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED;
    }

    public enum LeaveType {
        PRIVILEGE,MATERNITY,PATERNITY;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_id",unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "e_id")
    private Employee employee;

    @Column(name = "l_reason")
    private String reason;

    @Column(name = "l_applied_on")
    private LocalDate appliedOn;

    @Column(name = "l_from")
    private LocalDate from;

    @Column(name = "l_to")
    private LocalDate to;

    @Enumerated(EnumType.STRING)
    @Column(name = "l_status")
    private LeaveStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "l_type")
    private LeaveType type;

    @Column(name = "l_days")
    private double days;

    @Column(name = "l_month")
    private int month;

    @Column(name = "l_overflow")
    private double overflow ;

    @Override
    public String toString() {
        return "Leave{" +
                "id=" + id +
                ", employee=" + employee +
                ", reason='" + reason + '\'' +
                ", appliedOn=" + appliedOn +
                ", from=" + from +
                ", to=" + to +
                ", status=" + status +
                ", type=" + type +
                ", days=" + days +
                ", month=" + month +
                ", overflow=" + overflow +
                '}';
    }
}
