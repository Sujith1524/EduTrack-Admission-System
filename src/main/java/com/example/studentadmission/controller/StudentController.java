package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.service.StudentService;
import com.example.studentadmission.util.TokenUtility;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // --- 1. REGISTER (Unchanged logic) ---
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerStudent(@Valid @RequestBody Student student, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }
            response.put("message", "Validation Failed");
            response.put("status", "Error");
            response.put("errors", fieldErrors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Student newStudent = studentService.registerStudent(student);
            response.put("message", "Registration successful! Please login.");
            response.put("status", "Success");
            newStudent.setPassword(null);
            response.put("data", newStudent);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", "Registration failed: " + e.getMessage());
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // --- 2. LOGIN (Generates Access and Refresh Tokens) ---
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginStudent(@RequestBody Student studentLogin) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        try {
            Student student = studentService.loginStudent(studentLogin.getEmail(), studentLogin.getPassword());

            // Generate Access Token (10 mins) and Refresh Token (7 days)
            String accessToken = TokenUtility.generateAccessToken(student);
            String refreshToken = TokenUtility.generateRefreshToken(student);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("accessToken", accessToken);
            data.put("refreshToken", refreshToken); // New
            student.setPassword(null);
            data.put("studentDetails", student);

            response.put("message", "Login Successful");
            response.put("status", "Success");
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    // --- 3. TOKEN REFRESH ENDPOINT ---
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());
        String refreshToken = requestBody.get("refreshToken");

        if (refreshToken == null || !TokenUtility.validateToken(refreshToken)) {
            response.put("message", "Invalid Refresh Token.");
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (TokenUtility.isTokenExpired(refreshToken)) {
            response.put("message", "Refresh Token Expired. Please log in again.");
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get student ID from claims
            Map<String, Object> claims = TokenUtility.getClaims(refreshToken);
            Long studentId = (Long) claims.get("id");

            Student student = studentService.getStudentDetails(studentId);
            if (student == null) {
                throw new RuntimeException("Student not found.");
            }

            // Generate a brand new access token
            String newAccessToken = TokenUtility.generateAccessToken(student);

            response.put("message", "Access Token Refreshed Successfully");
            response.put("status", "Success");
            response.put("data", Map.of("accessToken", newAccessToken));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Token refresh failed: " + e.getMessage());
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    // --- 4. GET STUDENT PROFILE (Secured) ---
    // This is a test endpoint to verify the token/role logic
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStudentProfile(@PathVariable("id") Long studentId) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        Student student = studentService.getStudentDetails(studentId);

        if (student == null) {
            response.put("message", "Student not found");
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        student.setPassword(null);
        response.put("message", "Student details fetched successfully");
        response.put("status", "Success");
        response.put("data", student);

        return ResponseEntity.ok(response);
    }
}