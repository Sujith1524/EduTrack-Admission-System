package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.service.StudentService;
import com.example.studentadmission.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerStudent(@Valid @RequestBody Student student) {
        Student newStudent = studentService.registerStudent(student);

        // Wrap data in "student" key to match requirement
        Map<String, Object> dataWrapper = Collections.singletonMap("student", newStudent);

        return ResponseUtil.success(dataWrapper, "Student registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginStudent(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Map<String, Object> loginData = studentService.loginStudent(email, password);
        return ResponseUtil.success(loginData, "Login Successful", HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> requestBody) {
        Map<String, String> newTokenMap = studentService.refreshToken(requestBody.get("refreshToken"));
        return ResponseUtil.success(newTokenMap, "Access Token Refreshed Successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStudentProfile(@PathVariable("id") Long studentId) {
        Student student = studentService.getStudentDetails(studentId);

        // Wrap data in "student" key to match requirement
        Map<String, Object> dataWrapper = Collections.singletonMap("student", student);

        return ResponseUtil.success(dataWrapper, "Student details fetched successfully", HttpStatus.OK);
    }
}