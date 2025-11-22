package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Register Student
    @PostMapping("/register")
    public Student registerStudent(@RequestBody Student student) {
        return studentService.registerStudent(student);

    }

    // Login Student
    @PostMapping("/login")
    public ResponseEntity<String> loginStudent(@RequestBody Student studentLogin) {
        try {
            Student student = studentService.loginStudent(studentLogin.getEmail(), studentLogin.getPassword());
            return ResponseEntity.ok("Login Successful! Welcome " + student.getName());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Search Student Info
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long studentId) {
        Student student = studentService.getStudentDetails(studentId);

        if (student != null) {
            return ResponseEntity.ok(student);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
