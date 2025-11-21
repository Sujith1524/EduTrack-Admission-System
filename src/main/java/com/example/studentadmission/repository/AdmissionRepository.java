package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    // Task: Count Students for a Course
    // SQL equivalent: SELECT COUNT(*) FROM admission WHERE course_id = ?
    long countByCourseCourseId(Long courseId);

    // Task: Search Student Info (Find admission details by Student ID)
    List<Admission> findByStudentStudentId(Long studentId);
}
