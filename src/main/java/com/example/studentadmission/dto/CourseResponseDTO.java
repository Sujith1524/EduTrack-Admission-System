package com.example.studentadmission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
        "_class", "courseId", "courseName", "instituteId", "instituteName",
        "durationDays", "fees", "createdAt", "updatedAt"
})
public class CourseResponseDTO {

    // FIX: This field will now hold the Entity's class name, copied from the Entity in the controller.
    @JsonProperty("_class")
    private String _class;

    private Long courseId;
    private String courseName;

    // Institute details pulled up to the DTO level (Requires setInstituteId and setInstituteName)
    private Long instituteId;
    private String instituteName;

    private int durationDays;
    private BigDecimal fees;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}