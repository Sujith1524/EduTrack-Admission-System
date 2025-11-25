package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.service.InstituteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap; // Import LinkedHashMap
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/institutes")
public class InstituteController {

    @Autowired
    private InstituteService instituteService;

    // --- 1. ADD INSTITUTE (Ordered Response) ---
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addInstitute(@Valid @RequestBody Institute institute, BindingResult result) {

        // Use LinkedHashMap to preserve insertion order
        Map<String, Object> response = new LinkedHashMap<>();

        // 1. Package (First)
        response.put("package", this.getClass().getPackageName());

        // Check for Validation Errors
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }

            // Error Order: Package -> Message -> Status -> Errors
            response.put("message", "Validation Failed: Missing or Invalid Input");
            response.put("status", "Error");
            response.put("errors", fieldErrors);

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Success Logic
        Institute savedInstitute = instituteService.addInstitute(institute);

        // 2. Message (Second)
        response.put("message", "Institute added successfully");
        // 3. Status (Third)
        response.put("status", "Success");
        // 4. Data (Last)
        response.put("data", savedInstitute);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- 2. UPDATE INSTITUTE (Ordered Response) ---
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateInstitute(@PathVariable Long id, @RequestBody Institute instituteDetails) {

        Map<String, Object> response = new LinkedHashMap<>(); // Use LinkedHashMap
        response.put("package", this.getClass().getPackageName());

        Institute updatedInstitute = instituteService.updateInstitute(id, instituteDetails);

        if (updatedInstitute != null) {
            response.put("message", "Institute updated successfully");
            response.put("status", "Success");
            response.put("data", updatedInstitute);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Institute with ID " + id + " not found");
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInstitutes() {

        // Use LinkedHashMap to preserve order: Package -> Message -> Status -> Data
        Map<String, Object> response = new LinkedHashMap<>();

        // 1. Package
        response.put("package", this.getClass().getPackageName());

        List<Institute> institutes = instituteService.getAllInstitutes();

        if (institutes.isEmpty()) {
            // 2. Message
            response.put("message", "No institutes found");
            // 3. Status
            response.put("status", "Success");
            // 4. Data (Empty List)
            response.put("data", institutes);
        } else {
            // 2. Message
            response.put("message", "Institute list fetched successfully");
            // 3. Status
            response.put("status", "Success");
            // 4. Data (The actual list)
            response.put("data", institutes);
        }

        return ResponseEntity.ok(response);
    }
}