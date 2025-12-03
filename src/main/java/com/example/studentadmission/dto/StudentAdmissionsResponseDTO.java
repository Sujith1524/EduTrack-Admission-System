package com.example.studentadmission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.util.List;

@Data
@JsonPropertyOrder({
        "_class", "studentId", "studentName", "studentEmail",
        "admissionStatusMessage", "admissions"
})
public class StudentAdmissionsResponseDTO {

    // This will hold the Student Entity class path (e.g., com.example.studentadmission.entity.Student)
    @JsonProperty("_class")
    private String _class;

    private Long studentId;
    private String studentName;
    private String studentEmail;

    private String admissionStatusMessage;

    private List<AdmissionResponse> admissions;
}