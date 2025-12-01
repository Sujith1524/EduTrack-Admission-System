package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.service.InstituteService;
import com.example.studentadmission.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/institutes")
public class InstituteController {

    @Autowired
    private InstituteService instituteService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addInstitute(@Valid @RequestBody Institute institute) {
        Institute savedInstitute = instituteService.addInstitute(institute);
        return ResponseUtil.success(savedInstitute, "Institute added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateInstitute(@PathVariable Long id, @RequestBody Institute instituteDetails) {
        Institute updatedInstitute = instituteService.updateInstitute(id, instituteDetails);
        return ResponseUtil.success(updatedInstitute, "Institute updated successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInstitutes() {
        List<Institute> institutes = instituteService.getAllInstitutes();
        String message = institutes.isEmpty() ? "No institutes found" : "Institute list fetched successfully";
        return ResponseUtil.success(institutes, message, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteInstitute(@PathVariable("id") Long instituteId) {
        Institute deletedInstitute = instituteService.deleteInstitute(instituteId);
        return ResponseUtil.success(deletedInstitute, "Institute deleted successfully", HttpStatus.OK);
    }
}