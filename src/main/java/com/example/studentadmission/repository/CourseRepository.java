package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Course;
import com.example.studentadmission.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds a course by its name and the associated institute.
     * Required by CourseService for uniqueness validation.
     */
    Optional<Course> findByCourseNameAndInstitute(String courseName, Institute institute);

    /**
     * Finds all courses belonging to a specific institute.
     * Required by CourseService for fetching courses by institute ID.
     */
    List<Course> findByInstitute(Institute institute);
}