package com.example.studentadmission.service;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // --- POST CONSTRUCT: Ensure a SUPER_ADMIN exists ---
    @PostConstruct
    public void setupSuperAdmin() {
        if (studentRepository.findByRole(Student.Role.SUPER_ADMIN).isEmpty()) {
            Student superAdmin = new Student();
            superAdmin.setName("System Super Admin");
            superAdmin.setEmail("admin@super.com");
            // NOTE: In a real app, hash this password immediately!
            superAdmin.setPassword("SuperAdminPass123");
            superAdmin.setPhone("0000000000");
            superAdmin.setRole(Student.Role.SUPER_ADMIN);
            superAdmin.setCreatedAt(LocalDateTime.now());
            studentRepository.save(superAdmin);
            System.out.println("--- ALERT: Created default SUPER_ADMIN user ---");
        }
    }

    public Student registerStudent(Student student) {
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered.");
        }
        // Hash password here in a real application
        return studentRepository.save(student);
    }

    public Student loginStudent(String email, String password) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);

        if (studentOpt.isEmpty()) {
            throw new RuntimeException("Login failed: Invalid email or password.");
        }

        Student student = studentOpt.get();

        // NOTE: In a real app, compare HASHED passwords!
        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("Login failed: Invalid email or password.");
        }

        return student;
    }

    public Student getStudentDetails(Long studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }
}
