package com.example.studentadmission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.util.List;

@Data
@JsonPropertyOrder({
        "_class", "instituteId", "instituteName", "instituteEmail",
        "institutePhone", "instituteAddress", "courseStatusMessage", "courses"
})
public class InstituteCoursesResponseDTO {

    // FIX: This field is now a simple property that will be explicitly set
    // by the controller with the Institute Entity's class path.
    @JsonProperty("_class")
    private String _class;

    private Long instituteId;
    private String instituteName;
    private String instituteEmail;
    private String institutePhone;
    private String instituteAddress;

    private String courseStatusMessage;

    private List<CourseResponseDTO> courses;
}