package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    /**
     * Finds all Admission records associated with a specific Student ID.
     * FIX: Uses findByStudent_Id to correctly target the 'id' field within the 'student' relationship property.
     */
    List<Admission> findByStudent_Id(Long studentId);
}