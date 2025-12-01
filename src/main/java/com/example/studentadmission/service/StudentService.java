package com.example.studentadmission.service;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.repository.StudentRepository;
import com.example.studentadmission.util.TokenUtility;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @PostConstruct
    public void setupSuperAdmin() {
        if (studentRepository.findByRole(Student.Role.SUPER_ADMIN).isEmpty()) {
            Student superAdmin = new Student();
            superAdmin.setName("System Super Admin");
            superAdmin.setEmail("admin@super.com");
            superAdmin.setPassword("SuperAdminPass123"); // Hash this in real apps
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
        Student savedStudent = studentRepository.save(student);
        savedStudent.setPassword(null); // Clear password for response
        return savedStudent;
    }

    public Map<String, Object> loginStudent(String email, String password) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Login failed: Invalid email or password."));

        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("Login failed: Invalid email or password.");
        }

        String accessToken = TokenUtility.generateAccessToken(student);
        String refreshToken = TokenUtility.generateRefreshToken(student);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        student.setPassword(null);
        data.put("studentDetails", student);
        return data;
    }

    public Map<String, String> refreshToken(String refreshToken) {
        if (refreshToken == null || !TokenUtility.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token.");
        }
        if (TokenUtility.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh Token Expired. Please log in again.");
        }

        Map<String, Object> claims = TokenUtility.getClaims(refreshToken);
        Long studentId = (Long) claims.get("id");
        Student student = getStudentDetails(studentId);

        String newAccessToken = TokenUtility.generateAccessToken(student);
        return Map.of("accessToken", newAccessToken);
    }

    public Student getStudentDetails(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setPassword(null);
        return student;
    }
}