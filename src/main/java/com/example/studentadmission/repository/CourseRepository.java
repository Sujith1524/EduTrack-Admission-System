package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find courses where the associated Institute's ID matches
    List<Course> findByInstituteInstituteId(Long instituteId);
}
