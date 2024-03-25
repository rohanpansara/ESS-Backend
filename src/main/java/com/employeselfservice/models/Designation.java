package com.employeselfservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "designations")
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "d_id")
    private Long id;

    @Column(name = "d_name",unique = true,nullable = false)
    private String name;

    @Column(name = "d_description")
    private String description;

//    @OneToMany(mappedBy = "designation")
//    @JsonManagedReference
//    private List<Employee> employees;

    public Designation(String id) {
        System.out.println("Designation(String id)");
        this.id = Long.parseLong(id);
    }

    public Designation(Long id) {
        System.out.println("Designation(Long id)");
        this.id = id;
    }

    public Designation(int id) {
        System.out.println("Designation(int id)");
        this.id = (long) id;
    }

    @Override
    public String toString() {
        return "Designation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
