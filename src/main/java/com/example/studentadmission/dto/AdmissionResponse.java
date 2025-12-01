package com.example.studentadmission.dto;

import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.entity.Student;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AdmissionResponse {

    private Long admissionId;
    private Long studentId;
    private String studentName; // Assuming you want first name here
    private String studentPhone;
    private Long instituteId;
    private String instituteName;
    private Long courseId;
    private String courseName;
    private LocalDate admissionDate;
    private LocalDate completionDate;

    public static AdmissionResponse fromEntity(Admission admission) {
        AdmissionResponse dto = new AdmissionResponse();
        Student student = admission.getStudent();

        dto.setAdmissionId(admission.getAdmissionId());

        // --- FIX: Using getId() instead of getStudentId() ---
        dto.setStudentId(student.getId());

        // --- FIX: Using getFirstName() and getPhoneNumber() ---
        dto.setStudentName(student.getFirstName() + " " + student.getLastName());
        dto.setStudentPhone(student.getPhoneNumber());

        dto.setInstituteId(admission.getInstitute().getInstituteId());
        dto.setInstituteName(admission.getInstitute().getInstituteName());

        dto.setCourseId(admission.getCourse().getCourseId());
        dto.setCourseName(admission.getCourse().getCourseName());

        dto.setAdmissionDate(admission.getAdmissionDate());
        dto.setCompletionDate(admission.getCompletionDate());

        return dto;
    }
}