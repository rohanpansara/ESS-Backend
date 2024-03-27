package com.employeselfservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notifications")
public class Notifications {

    public enum NotificationType{
        WORK,HR,OFFICE_EVENT,UPDATES
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "e_id",nullable = false)
    @JsonManagedReference
    private Employee employee;

    @Column(name = "n_notification", nullable = false)
    private String notification;

    @Column(name = "n_created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "n_type")
    private Notifications.NotificationType type;

}
