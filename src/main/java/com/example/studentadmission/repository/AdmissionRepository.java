package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    // Count Students for a Course
    long countByCourseCourseId(Long courseId);

    // Search Student Info (Find admission details by Student ID)
    List<Admission> findByStudentStudentId(Long studentId);
}
