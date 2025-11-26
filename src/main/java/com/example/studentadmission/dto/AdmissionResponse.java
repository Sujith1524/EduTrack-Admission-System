package com.example.studentadmission.dto;

import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.entity.Course;
import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.entity.Student;
import lombok.Data;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AdmissionResponse {

    private Long admissionId;
    private LocalDate admissionDate;
    private LocalDate completionDate;

    // Custom fields to hold simplified, ordered data
    private Map<String, Object> studentDetails;
    private Institute instituteDetails; // Institute can stay full for now, if desired
    private Map<String, Object> courseDetails;

    // Static factory method to convert the Admission Entity into a clean DTO
    public static AdmissionResponse fromEntity(Admission admission) {
        AdmissionResponse dto = new AdmissionResponse();

        dto.setAdmissionId(admission.getAdmissionId());
        dto.setAdmissionDate(admission.getAdmissionDate());
        dto.setCompletionDate(admission.getCompletionDate());

        // --- 1. Student Details (Simplified and Password Removed) ---
        Student student = admission.getStudent();
        Map<String, Object> studentMap = new LinkedHashMap<>();
        studentMap.put("studentId", student.getStudentId());
        studentMap.put("name", student.getName());
        studentMap.put("email", student.getEmail());
        studentMap.put("phone", student.getPhone());
        studentMap.put("createdAt", student.getCreatedAt());
        dto.setStudentDetails(studentMap);

        // --- 2. Institute Details (Full Institute) ---
        dto.setInstituteDetails(admission.getInstitute());

        // --- 3. Course Details (Simplified - ONLY ID, Name, Duration) ---
        Course course = admission.getCourse();
        Map<String, Object> courseMap = new LinkedHashMap<>();
        courseMap.put("courseId", course.getCourseId());
        courseMap.put("courseName", course.getCourseName());
        courseMap.put("durationDays", course.getDurationDays());
        // *** CRITICAL: We STOP here, preventing the nested Institute details ***
        dto.setCourseDetails(courseMap);

        return dto;
    }
}