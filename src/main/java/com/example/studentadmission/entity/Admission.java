package com.example.studentadmission.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

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
    @NotNull(message = "Student is required for admission")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "institute_id")
    @NotNull(message = "Institute is required for admission")
    private Institute institute;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @NotNull(message = "Course is required for admission")
    private Course course;
}
