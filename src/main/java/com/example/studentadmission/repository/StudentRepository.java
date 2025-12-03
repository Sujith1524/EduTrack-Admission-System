package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // FIX: Changed back to findByStudentEmail to match the latest Student Entity field
    Optional<Student> findByStudentEmail(String email);

    List<Student> findByRole(Student.Role role);
}