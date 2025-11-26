package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);

    // New method to find students by role (used for checking if SUPER_ADMIN exists)
    List<Student> findByRole(Student.Role role);
}