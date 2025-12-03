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
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate; // Keeping import, but logic is removed.

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

    @PostConstruct
    @Transactional
    public void setupSuperAdmin() {
        if (studentRepository.findByRole(Student.Role.SUPER_ADMIN).isEmpty()) {

            ClassInfo adminClass = new ClassInfo();
            adminClass.setClassId("ADMIN");
            adminClass.setClassName("Administration");
            adminClass.setSection("NA");
            adminClass.setAcademicYear("2000-2001");

            // Explicitly save the dependent ClassInfo object first.
            ClassInfo persistedAdminClass = classInfoRepository.save(adminClass);

            Address adminAddress = new Address();
            adminAddress.setHouseName("System House");
            adminAddress.setStreet("NA");
            adminAddress.setPost("NA");
            adminAddress.setCity("Headquarters");
            adminAddress.setState("System");
            adminAddress.setPinCode("000000");

            Student superAdmin = new Student();

            superAdmin.setStudentName("System Admin");
            superAdmin.setFirstName("System");
            superAdmin.setLastName("Admin");
            superAdmin.setStudentEmail("admin@super.com");
            superAdmin.setPassword("SuperAdminPass123");
            superAdmin.setPhoneNumber("0000000000");
            superAdmin.setRole(Student.Role.SUPER_ADMIN);
            superAdmin.setDateOfBirth(LocalDate.of(1980, 1, 1));
            superAdmin.setGender(Student.Gender.Other);

            superAdmin.setAddress(adminAddress);
            superAdmin.setClassInfo(persistedAdminClass);

            studentRepository.save(superAdmin);
            System.out.println(">>> Super Admin account created successfully <<<");
        }
    }

    @Transactional
    public Student registerStudent(Student student) {
        if (studentRepository.findByStudentEmail(student.getStudentEmail()).isPresent()) {
            throw new RuntimeException("Email already registered.");
        }

        String incomingClassId = student.getClassInfo().getClassId();
        Optional<ClassInfo> existingClass = classInfoRepository.findById(incomingClassId);

        if (existingClass.isPresent()) {
            // Case 1: Class exists. Link the student to the managed instance.
            student.setClassInfo(existingClass.get());
        } else {
            // Case 2: Class does NOT exist (it is a transient object from the request).
            // We must explicitly save it before saving the Student.
            ClassInfo newClass = classInfoRepository.save(student.getClassInfo());
            student.setClassInfo(newClass);
        }

        return studentRepository.save(student);
    }

    @Transactional
    public Map<String, Object> loginStudent(String email, String password) {
        Student student = studentRepository.findByStudentEmail(email)
                .orElseThrow(() -> new RuntimeException("Login failed: Invalid email or password."));

        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("Login failed: Invalid email or password.");
        }

        if (student.getStatus() == Student.Status.INACTIVE) {
            throw new RuntimeException("Login failed: Account is Inactive.");
        }

        // REMOVED: Hibernate.initialize() calls because EAGER fetching is now used in the entity.

        String accessToken = TokenUtility.generateAccessToken(student);
        String refreshToken = TokenUtility.generateRefreshToken(student);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        data.put("student", student);

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
        Long studentId = ((Number) claims.get("id")).longValue();
        Student student = getStudentDetails(studentId);

        String newAccessToken = TokenUtility.generateAccessToken(student);
        return Map.of("accessToken", newAccessToken);
    }

    @Transactional(readOnly = true)
    public Student getStudentDetails(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // REMOVED: Hibernate.initialize() calls because EAGER fetching is now used in the entity.

        return student;
    }
}