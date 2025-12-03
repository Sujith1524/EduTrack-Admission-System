package com.example.studentadmission.dto;

import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.entity.Course;
import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.entity.Student;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonPropertyOrder({
        "_class", "admissionId", "studentId", "studentName", "studentPhone",
        "institute", "course", "admissionDate", "completionDate"
})
public class AdmissionResponse {

    @JsonProperty("_class")
    public String get_class() {
        return this.getClass().getName();
    }

    private Long admissionId;

    // Keeping student details to match the specific JSON output structure provided by the user
    private Long studentId;
    private String studentName;
    private String studentPhone;

    // FIX: Using full Institute and Course entities to show all details and their _class property
    private Institute institute;
    private Course course;

    private LocalDate admissionDate;
    private LocalDate completionDate;

    public static AdmissionResponse fromEntity(Admission admission) {
        AdmissionResponse dto = new AdmissionResponse();
        Student student = admission.getStudent();

        dto.setAdmissionId(admission.getAdmissionId());

        dto.setStudentId(student.getStudentId());
        dto.setStudentName(student.getStudentName());
        dto.setStudentPhone(student.getPhoneNumber());

        // FIX: Set the full entities which will trigger their serialization
        dto.setInstitute(admission.getInstitute());
        dto.setCourse(admission.getCourse());

        dto.setAdmissionDate(admission.getAdmissionDate());
        dto.setCompletionDate(admission.getCompletionDate());

        return dto;
    }
}