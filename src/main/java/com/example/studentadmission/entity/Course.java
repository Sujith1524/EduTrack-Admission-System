package com.example.studentadmission.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "course")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private String courseName;

    private Integer durationDays;

    // Relationship: Link to the Institute Entity
    // 'institute_id' will be the foreign key column in the course table
    @ManyToOne
    @JoinColumn(name = "institute_id")
    private Institute institute;
}
