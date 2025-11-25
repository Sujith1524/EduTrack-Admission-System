package com.example.studentadmission.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// This DTO defines the exact structure we want in the JSON response for a Course list.
@Data
@NoArgsConstructor
public class CourseResponseDTO {
    private Long courseId;
    private String courseName;
    private Integer durationDays;
}
