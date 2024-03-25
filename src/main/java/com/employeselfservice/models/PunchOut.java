package com.employeselfservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "punch_outs")
public class PunchOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_out_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "e_id")
    @JsonManagedReference
    private Employee employee;

    @Column(name = "p_out_time")
    private LocalDateTime punchOutTime;

    public PunchOut(Employee employee){
        this.employee=employee;
        this.punchOutTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "PunchOut{" +
                "id=" + id +
                ", employee=" + employee.getFirstname().trim()+" "+employee.getLastname().trim() +
                ", punchInTime=" + punchOutTime +
                '}';
    }
}
