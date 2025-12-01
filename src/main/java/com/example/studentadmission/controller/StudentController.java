package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.service.StudentService;
import com.example.studentadmission.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerStudent(@Valid @RequestBody Student student) {
        Student newStudent = studentService.registerStudent(student);
        return ResponseUtil.success(newStudent, "Registration successful! Please login.", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginStudent(@RequestBody Student studentLogin) {
        Map<String, Object> loginData = studentService.loginStudent(studentLogin.getEmail(), studentLogin.getPassword());
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
        return ResponseUtil.success(student, "Student details fetched successfully", HttpStatus.OK);
    }
}