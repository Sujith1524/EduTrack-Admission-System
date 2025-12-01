package com.example.studentadmission.service;

import com.example.studentadmission.entity.ClassInfo;
import com.example.studentadmission.entity.Student;
import com.example.studentadmission.repository.ClassInfoRepository;
import com.example.studentadmission.repository.StudentRepository;
import com.example.studentadmission.util.TokenUtility;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassInfoRepository classInfoRepository;

    @PostConstruct
    public void setupSuperAdmin() {
        if (studentRepository.findByRole(Student.Role.SUPER_ADMIN).isEmpty()) {
            Student superAdmin = new Student();
            superAdmin.setFirstName("System");
            superAdmin.setLastName("SuperAdmin");
            superAdmin.setEmail("admin@super.com");
            superAdmin.setPassword("SuperAdminPass123");
            superAdmin.setPhoneNumber("0000000000");
            superAdmin.setRole(Student.Role.SUPER_ADMIN);
            superAdmin.setDateOfBirth(LocalDate.of(2000, 1, 1));
            superAdmin.setGender(Student.Gender.Male);
            // Note: Address and ClassInfo are required by entity validation,
            // but for a system admin script, you might bypass or create dummies.
            // Skipping detailed dummy creation here for brevity.
            // In a real app, create a dummy Admin Address/Class.
        }
    }

    @Transactional
    public Student registerStudent(Student student) {
        // 1. Check Email
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered.");
        }

        // 2. Handle Class Info Logic
        // If the Class ID exists in DB, use that record. If not, save the new one provided.
        // This prevents duplicate rows for "Computer Science - Section A"
        String incomingClassId = student.getClassInfo().getClassId();
        Optional<ClassInfo> existingClass = classInfoRepository.findById(incomingClassId);

        if (existingClass.isPresent()) {
            student.setClassInfo(existingClass.get());
        } else {
            // The cascade configuration in Student entity will save this new ClassInfo
            // automatically, but we can also explicit save if needed.
            // Relying on CascadeType.PERSIST defined in Student.java
        }

        // 3. Save Student (Address saves automatically due to CascadeType.ALL)
        return studentRepository.save(student);
    }

    public Map<String, Object> loginStudent(String email, String password) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Login failed: Invalid email or password."));

        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("Login failed: Invalid email or password.");
        }

        if (student.getStatus() == Student.Status.INACTIVE) {
            throw new RuntimeException("Login failed: Account is Inactive.");
        }

        String accessToken = TokenUtility.generateAccessToken(student);
        String refreshToken = TokenUtility.generateRefreshToken(student);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);

        // Structure the student object within "student" key if needed for login response too
        data.put("student", student);

        return data;
    }

    // Refresh Token Logic remains the same...
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
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
}