package com.example.studentadmission.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate; // Use LocalDate for dates without time

@Entity
@Table(name = "admission")
@Data
public class Admission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admissionId;

    private LocalDate admissionDate;
    private LocalDate completionDate;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "institute_id")
    private Institute institute;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
