package com.example.studentadmission.service;

import com.example.studentadmission.entity.Course;
import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.repository.CourseRepository;
import com.example.studentadmission.repository.InstituteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstituteRepository instituteRepository;

    public Course addCourse(Course course) {
        // 1. Validate Institute existence
        Institute institute = instituteRepository.findById(course.getInstitute().getInstituteId())
                .orElseThrow(() -> new RuntimeException("Institute not found with ID: " + course.getInstitute().getInstituteId()));

        // 2. Check for uniqueness: A course with the same name shouldn't exist in the same institute.
        Optional<Course> existingCourse = courseRepository.findByCourseNameAndInstitute(course.getCourseName(), institute);
        if (existingCourse.isPresent()) {
            throw new RuntimeException("Course with name '" + course.getCourseName() +
                    "' already exists at institute '" + institute.getInstituteName() + "'.");
        }

        course.setInstitute(institute);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long courseId, Course updatedCourse) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        // 1. Validate Institute existence
        Institute institute = instituteRepository.findById(updatedCourse.getInstitute().getInstituteId())
                .orElseThrow(() -> new RuntimeException("Institute not found with ID: " + updatedCourse.getInstitute().getInstituteId()));

        // 2. Check for uniqueness against other courses (excluding the current one)
        Optional<Course> existingByNameAndInstitute = courseRepository.findByCourseNameAndInstitute(
                updatedCourse.getCourseName(),
                institute
        );

        if (existingByNameAndInstitute.isPresent() && !existingByNameAndInstitute.get().getCourseId().equals(courseId)) {
            throw new RuntimeException("Course with name '" + updatedCourse.getCourseName() +
                    "' already exists at institute '" + institute.getInstituteName() + "'.");
        }

        // 3. Apply updates
        existingCourse.setCourseName(updatedCourse.getCourseName());
        existingCourse.setInstitute(institute); // Update the institute if changed
        existingCourse.setDurationDays(updatedCourse.getDurationDays());
        existingCourse.setFees(updatedCourse.getFees());

        return courseRepository.save(existingCourse);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
    }

    // Gets all courses associated with a specific institute ID.
    public List<Course> getCoursesByInstituteId(Long instituteId) {
        Institute institute = instituteRepository.findById(instituteId)
                .orElseThrow(() -> new RuntimeException("Institute not found with ID: " + instituteId));

        // This relies on the findByInstitute method in CourseRepository
        return courseRepository.findByInstitute(institute);
    }

    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        courseRepository.deleteById(courseId);
    }

    public List<Course> getAllCourseList() {
        return courseRepository.findAll();
    }

}