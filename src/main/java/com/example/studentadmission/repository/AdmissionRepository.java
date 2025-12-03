package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    // FIX: Updated to match the studentId field in Student entity
    List<Admission> findByStudent_StudentId(Long studentId);
}