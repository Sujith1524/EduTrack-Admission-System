package com.example.studentadmission.controller;

import com.example.studentadmission.dto.AdmissionResponse;
import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.service.AdmissionService;
import com.example.studentadmission.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admissions")
public class AdmissionController {

    @Autowired
    private AdmissionService admissionService;

    @PostMapping("/take")
    public ResponseEntity<Map<String, Object>> takeAdmission(@Valid @RequestBody Admission admission) {
        AdmissionResponse admissionDto = admissionService.takeAdmission(admission);
        return ResponseUtil.success(admissionDto, "Admission taken successfully", HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentAdmissions(@PathVariable Long studentId) {
        List<AdmissionResponse> admissionDtos = admissionService.getAdmissionsByStudent(studentId);
        return ResponseUtil.success(admissionDtos, "Admissions records fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/duration-left/{admissionId}")
    public ResponseEntity<Map<String, Object>> checkDuration(@PathVariable Long admissionId) {
        String duration = admissionService.getDurationLeft(admissionId);
        return ResponseUtil.success(Map.of("admissionId", admissionId, "durationStatus", duration), "Duration check successful", HttpStatus.OK);
    }
}