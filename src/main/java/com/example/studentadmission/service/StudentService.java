package com.example.studentadmission.service;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // Register Student
    public Student registerStudent(Student student) {

        return studentRepository.save(student);
    }

    // Login Student
    public Student loginStudent(String email, String password) {
        Student student = studentRepository.findByEmail(email);

        // Check if student exists and password matches
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }

        throw new RuntimeException("Login Failed: Invalid Email or Password");
    }

    // Search Student Info
    public Student getStudentDetails(Long studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }
}
