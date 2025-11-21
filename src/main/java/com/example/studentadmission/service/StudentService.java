package com.example.studentadmission.service;


import com.example.studentadmission.entity.Student;
import com.example.studentadmission.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired // Inject the repository
    private StudentRepository studentRepository;

    // Task 3: Register Student (CREATE operation)
    public Student registerStudent(Student student) {
        // Here you would add password hashing logic (e.g., using BCrypt)
        return studentRepository.save(student);
    }

    // Task 3: Login Student (READ operation)
    public Student loginStudent(String email, String password) {
        Student student = studentRepository.findByEmail(email);

        // Simple, insecure check - use password hashing in a real app!
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }
        return null; // Login failed
    }

    // Task 3: Search Student Info (READ by ID)
    public Student getStudentDetails(Long studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }
}
