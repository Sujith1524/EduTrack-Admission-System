package com.example.studentadmission.service;

import com.example.studentadmission.entity.Address;
import com.example.studentadmission.entity.ClassInfo;
import com.example.studentadmission.entity.Student;
import com.example.studentadmission.repository.ClassInfoRepository;
import com.example.studentadmission.repository.StudentRepository;
import com.example.studentadmission.util.TokenUtility;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import added

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassInfoRepository classInfoRepository;

    /**
     * FIX: Added @Transactional to ensure database operations succeed.
     * FIX: Added required Address and ClassInfo fields for Super Admin to pass validation.
     */
    @PostConstruct
    @Transactional
    public void setupSuperAdmin() {
        if (studentRepository.findByRole(Student.Role.SUPER_ADMIN).isEmpty()) {

            // 1. Create Dummy ClassInfo (Required)
            ClassInfo adminClass = new ClassInfo();
            adminClass.setClassId("ADMIN");
            adminClass.setClassName("Administration");
            adminClass.setSection("NA");
            adminClass.setAcademicYear("2000-2001");
            // The ClassInfo entity will be saved automatically via cascade,
            // but we ensure we have the object ready.

            // 2. Create Dummy Address (Required)
            Address adminAddress = new Address();
            adminAddress.setHouseName("System House");
            adminAddress.setStreet("NA");
            adminAddress.setPost("NA");
            adminAddress.setCity("Headquarters");
            adminAddress.setState("System");
            adminAddress.setPinCode("000000");

            // 3. Create Super Admin Student
            Student superAdmin = new Student();
            superAdmin.setFirstName("System");
            superAdmin.setLastName("Admin"); // Added last name
            superAdmin.setEmail("admin@super.com");
            superAdmin.setPassword("SuperAdminPass123");
            superAdmin.setPhoneNumber("0000000000");
            superAdmin.setRole(Student.Role.SUPER_ADMIN);
            superAdmin.setDateOfBirth(LocalDate.of(1980, 1, 1)); // Realistic DOB
            superAdmin.setGender(Student.Gender.Other); // Set a default gender

            // 4. Set required relationships
            superAdmin.setAddress(adminAddress);
            superAdmin.setClassInfo(adminClass);

            // 5. Save
            studentRepository.save(superAdmin);
            System.out.println(">>> Super Admin account created successfully <<<");
        }
    }

    @Transactional
    public Student registerStudent(Student student) {
        // 1. Check Email
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered.");
        }

        // 2. Handle Class Info Logic: Check if class exists, otherwise use/save the new one.
        String incomingClassId = student.getClassInfo().getClassId();
        Optional<ClassInfo> existingClass = classInfoRepository.findById(incomingClassId);

        if (existingClass.isPresent()) {
            student.setClassInfo(existingClass.get());
        } // If not present, the new ClassInfo instance will be saved via CascadeType.PERSIST

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
        // Note: JWT claims store numbers as Integer/Double/Long, cast carefully
        Long studentId = ((Number) claims.get("id")).longValue();
        Student student = getStudentDetails(studentId);

        String newAccessToken = TokenUtility.generateAccessToken(student);
        return Map.of("accessToken", newAccessToken);
    }

    public Student getStudentDetails(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
}