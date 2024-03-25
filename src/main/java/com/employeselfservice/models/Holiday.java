package com.employeselfservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "holiday")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "h_id")
    private Long id;

    @Column(name = "h_from", nullable = false)
    private LocalDate from;

    @Column(name = "h_to", nullable = true)
    private LocalDate to;

    @Column(name = "h_days", nullable = false)
    private int days;

    @Column(name = "h_name", nullable = false, unique = true)
    private String name;

    @Column(name = "h_description", nullable = true)
    private String description;

}
