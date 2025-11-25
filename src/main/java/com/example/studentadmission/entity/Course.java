package com.example.studentadmission.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "course")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @NotBlank(message = "Course Name is Required")
    private String courseName;

    @NotNull(message = "Duration in days is Required")
    @Min(value = 15, message = "Duration must be at least 15 days")
    private Integer durationDays;

    // Relationship: Link to the Institute Entity
    @ManyToOne
    @JoinColumn(name = "institute_id")
    @NotNull(message = "Institute ID is Required for the course")
    private Institute institute;
}
