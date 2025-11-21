package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.service.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admissions")
public class AdmissionController {

    @Autowired
    private AdmissionService admissionService;

    // 1. Take Admission
    @PostMapping("/take")
    public ResponseEntity<Admission> takeAdmission(@RequestBody Admission admission) {
        return ResponseEntity.ok(admissionService.takeAdmission(admission));
    }

    // 2. Count Students for a specific Course
    @GetMapping("/count/{courseId}")
    public ResponseEntity<Map<String, Long>> countStudents(@PathVariable Long courseId) {
        long count = admissionService.getStudentCountByCourse(courseId);
        // Returning a simple JSON object { "count": 5 }
        return ResponseEntity.ok(Map.of("count", count));
    }

    // 3. Check Duration Left for an Admission
    @GetMapping("/duration-left/{admissionId}")
    public ResponseEntity<String> checkDuration(@PathVariable Long admissionId) {
        return ResponseEntity.ok(admissionService.getDurationLeft(admissionId));
    }

    // 4. Search Student Info (Get full admission details for a student)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Admission>> getStudentAdmissions(@PathVariable Long studentId) {
        return ResponseEntity.ok(admissionService.getAdmissionsByStudent(studentId));
    }
}
