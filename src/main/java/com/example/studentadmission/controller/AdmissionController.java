package com.example.studentadmission.controller;

import com.example.studentadmission.dto.AdmissionResponse;
import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.service.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Import Streams

@RestController
@RequestMapping("/admissions")
public class AdmissionController {

    @Autowired
    private AdmissionService admissionService;

    // 1. Take Admission (Structured Response - Modified to use the clean DTO logic)
    @PostMapping("/take")
    public ResponseEntity<Map<String, Object>> takeAdmission(@Valid @RequestBody Admission admission, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

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

            // Use the DTO to structure the response cleanly
            AdmissionResponse admissionDto = AdmissionResponse.fromEntity(newAdmission);

            response.put("message", "Admission taken successfully");
            response.put("status", "Success");

            // Custom response object mirroring the DTO structure for clarity
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("admissionId", admissionDto.getAdmissionId());
            data.put("admissionDate", admissionDto.getAdmissionDate());
            data.put("completionDate", admissionDto.getCompletionDate());
            data.put("studentDetails", admissionDto.getStudentDetails());
            data.put("instituteDetails", admissionDto.getInstituteDetails());
            data.put("courseDetails", admissionDto.getCourseDetails());

            response.put("data", data);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // 4. Search Student Info (Updated to use the new DTO list)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentAdmissions(@PathVariable Long studentId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        List<Admission> admissions = admissionService.getAdmissionsByStudent(studentId);

        // Convert the list of Admission Entities to a list of AdmissionResponse DTOs
        List<AdmissionResponse> admissionDtos = admissions.stream()
                .map(AdmissionResponse::fromEntity)
                .collect(Collectors.toList());

        response.put("message", "Admissions records fetched successfully");
        response.put("status", "Success");
        response.put("data", admissionDtos); // Return the clean DTO list
        return ResponseEntity.ok(response);
    }

//    // 2. Count Students (Unchanged)
//    @GetMapping("/count/{courseId}")
//    public ResponseEntity<Map<String, Object>> countStudents(@PathVariable Long courseId) {
//        Map<String, Object> response = new LinkedHashMap<>();
//        response.put("package", this.getClass().getPackageName());
//        long count = admissionService.getStudentCountByCourse(courseId);
//        response.put("message", "Student count fetched successfully");
//        response.put("status", "Success");
//        response.put("data", Map.of("courseId", courseId, "studentCount", count));
//        return ResponseEntity.ok(response);
//    }

    // 3. Check Duration (Unchanged)
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
}