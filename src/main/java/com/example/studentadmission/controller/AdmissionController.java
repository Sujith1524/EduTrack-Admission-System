package com.example.studentadmission.controller;

import com.example.studentadmission.dto.AdmissionResponse;
import com.example.studentadmission.dto.StudentAdmissionsResponseDTO;
import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.entity.Student;
import com.example.studentadmission.service.AdmissionService;
import com.example.studentadmission.service.StudentService;
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

    @Autowired
    private StudentService studentService;

    @PostMapping("/take")
    public ResponseEntity<Map<String, Object>> takeAdmission(@Valid @RequestBody Admission admission) {
        try {
            AdmissionResponse admissionDto = admissionService.takeAdmission(admission);
            return ResponseUtil.success(admissionDto, "Admission taken successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) { // Catch more specific exception first (e.g., missing ID)
            // FIX: Swapped order. Catching specific IllegalArgumentException here.
            return ResponseUtil.error(null, e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) { // Catch broader RuntimeException last (e.g., entity not found)
            // FIX: Swapped order. This will catch Runtimes that are NOT IllegalArgumentException.
            return ResponseUtil.error(null, e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentAdmissions(@PathVariable Long studentId) {

        // 1. Fetch Student Details (Throws error if student doesn't exist)
        Student student = studentService.getStudentDetails(studentId);

        // 2. Fetch Admissions List
        List<AdmissionResponse> admissionDtos = admissionService.getAdmissionsByStudent(studentId);

        // 3. Construct the Response DTO
        StudentAdmissionsResponseDTO responseDto = new StudentAdmissionsResponseDTO();

        // Use the Student Entity class path for _class
        responseDto.set_class(student.get_class());

        responseDto.setStudentId(student.getStudentId());
        responseDto.setStudentName(student.getStudentName());
        responseDto.setStudentEmail(student.getStudentEmail());

        if (admissionDtos.isEmpty()) {
            responseDto.setAdmissionStatusMessage("No admissions found for this student.");
            // admissions list defaults to null
        } else {
            responseDto.setAdmissionStatusMessage("Admissions fetched successfully.");
            responseDto.setAdmissions(admissionDtos);
        }

        return ResponseUtil.success(responseDto, "Admissions records fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/duration-left/{admissionId}")
    public ResponseEntity<Map<String, Object>> checkDuration(@PathVariable Long admissionId) {
        String duration = admissionService.getDurationLeft(admissionId);
        return ResponseUtil.success(Map.of("admissionId", admissionId, "durationStatus", duration), "Duration check successful", HttpStatus.OK);
    }
}