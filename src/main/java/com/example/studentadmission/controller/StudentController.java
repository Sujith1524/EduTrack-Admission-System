package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.service.StudentService;
import com.example.studentadmission.util.TokenUtility;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult; // Import BindingResult
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // --- 1. STUDENT REGISTRATION (With Validations) ---
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerStudent(@Valid @RequestBody Student student, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        // 1. Check for Validation Errors
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }

            response.put("message", "Validation Failed: Invalid Input");
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

    // --- 2. LOGIN (Kept simple, service handles business logic checks) ---
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginStudent(@RequestBody Student studentLogin) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        try {
            Student student = studentService.loginStudent(studentLogin.getEmail(), studentLogin.getPassword());
            String token = TokenUtility.generateToken(student);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("accessToken", token);
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

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long studentId) {
        Student student = studentService.getStudentDetails(studentId);
        return student != null ? ResponseEntity.ok(student) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}