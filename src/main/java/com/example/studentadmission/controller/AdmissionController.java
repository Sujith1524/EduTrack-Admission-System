package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.entity.Student;
import com.example.studentadmission.service.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admissions")
public class AdmissionController {

    @Autowired
    private AdmissionService admissionService;

    // 1. Take Admission (Structured Response)
    @PostMapping("/take")
    public ResponseEntity<Map<String, Object>> takeAdmission(@RequestBody Admission admission) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        try {
            Admission newAdmission = admissionService.takeAdmission(admission);

            response.put("message", "Admission taken successfully");
            response.put("status", "Success");

            // --- Custom Data Structure Construction ---
            Map<String, Object> data = new LinkedHashMap<>();

            // 1. Student Details (First, as requested)
            // Clone student to safely remove password without affecting DB object reference
            Student student = newAdmission.getStudent();
            Map<String, Object> studentMap = new LinkedHashMap<>();
            studentMap.put("studentId", student.getStudentId());
            studentMap.put("name", student.getName());
            studentMap.put("email", student.getEmail());
            studentMap.put("phone", student.getPhone());
            studentMap.put("createdAt", student.getCreatedAt());
            data.put("studentDetails", studentMap);

            // 2. Institute Details (Second)
            data.put("instituteDetails", newAdmission.getInstitute());

            // 3. Course Details (Third - Cleaned up to remove nested Institute)
            Map<String, Object> courseMap = new LinkedHashMap<>();
            courseMap.put("courseId", newAdmission.getCourse().getCourseId());
            courseMap.put("courseName", newAdmission.getCourse().getCourseName());
            courseMap.put("durationDays", newAdmission.getCourse().getDurationDays());
            data.put("courseDetails", courseMap);

            // 4. Admission Specifics (Last)
            data.put("admissionId", newAdmission.getAdmissionId());
            data.put("admissionDate", newAdmission.getAdmissionDate());
            data.put("completionDate", newAdmission.getCompletionDate());

            response.put("data", data);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // ... (Keep other methods: countStudents, checkDuration, getStudentAdmissions) ...

    @GetMapping("/count/{courseId}")
    public ResponseEntity<Map<String, Object>> countStudents(@PathVariable Long courseId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());
        long count = admissionService.getStudentCountByCourse(courseId);
        response.put("message", "Student count fetched successfully");
        response.put("status", "Success");
        response.put("data", Map.of("courseId", courseId, "studentCount", count));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/duration-left/{admissionId}")
    public ResponseEntity<Map<String, Object>> checkDuration(@PathVariable Long admissionId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());
        String duration = admissionService.getDurationLeft(admissionId);
        response.put("message", "Duration check successful");
        response.put("status", "Success");
        response.put("data", Map.of("admissionId", admissionId, "durationStatus", duration));
        if (duration.equals("Admission not found")) {
            response.put("message", duration);
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentAdmissions(@PathVariable Long studentId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());
        List<Admission> admissions = admissionService.getAdmissionsByStudent(studentId);
        response.put("message", "Admissions records fetched successfully");
        response.put("status", "Success");
        response.put("data", admissions);
        return ResponseEntity.ok(response);
    }
}