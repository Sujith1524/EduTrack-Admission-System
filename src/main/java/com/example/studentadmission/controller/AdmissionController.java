package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.service.AdmissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionController {

    @Autowired
    private AdmissionService admissionService;

    // 1. Take Admission (With Validations)
    @PostMapping("/take")
    public ResponseEntity<Map<String, Object>> takeAdmission(@Valid @RequestBody Admission admission, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        // 1. Validation Check (Ensures Student, Course, Institute IDs are provided)
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }
            response.put("message", "Validation Failed: Missing Required IDs");
            response.put("status", "Error");
            response.put("errors", fieldErrors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Admission newAdmission = admissionService.takeAdmission(admission);
            response.put("message", "Admission taken successfully");
            response.put("status", "Success");
            response.put("data", newAdmission);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // (Keep other methods countStudents, checkDuration, getStudentAdmissions the same...)
    // ...
    // 2. Count Students
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

    // 3. Check Duration
    @GetMapping("/duration-left/{admissionId}")
    public ResponseEntity<Map<String, Object>> checkDuration(@PathVariable Long admissionId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());
        String duration = admissionService.getDurationLeft(admissionId);
        response.put("message", "Duration check successful");
        response.put("status", "Success");
        response.put("data", Map.of("admissionId", admissionId, "durationStatus", duration));
        return ResponseEntity.ok(response);
    }

    // 4. Search Student Info
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