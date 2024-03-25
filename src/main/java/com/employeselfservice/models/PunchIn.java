package com.employeselfservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "punch_ins")
public class PunchIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_in_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "e_id",nullable = false)
    @JsonManagedReference
    private Employee employee;

    @Column(name = "p_in_time",nullable = false)
    private LocalDateTime punchInTime;

    public PunchIn(Employee employee){
        this.employee=employee;
        this.punchInTime = LocalDateTime.now();
    }
}
