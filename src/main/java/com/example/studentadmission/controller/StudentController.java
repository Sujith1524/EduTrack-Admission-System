package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Marks this class as a REST API handler
@RequestMapping("/students") // Base URL for all endpoints in this controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Endpoint for Task 3: Register Student
    @PostMapping("/register") // POST request to /api/students/register
    public ResponseEntity<Student> registerStudent(@RequestBody Student student) {
        Student newStudent = studentService.registerStudent(student);
        // Returns the created student and HTTP status 201 (Created)
        return new ResponseEntity<>(newStudent, HttpStatus.CREATED);
    }

    // Endpoint for Task 3: Login Student
    @PostMapping("/login") // POST request to /api/students/login
    public ResponseEntity<String> loginStudent(@RequestBody Student studentLogin) {
        Student student = studentService.loginStudent(studentLogin.getEmail(), studentLogin.getPassword());

        if (student != null) {
            return ResponseEntity.ok("Login Successful! Welcome " + student.getName());
        } else {
            return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    // Endpoint for Task 3: Search Student Info
    @GetMapping("/{id}") // GET request to /api/students/{id}
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long studentId) {
        Student student = studentService.getStudentDetails(studentId);

        if (student != null) {
            return ResponseEntity.ok(student);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
