package com.example.studentadmission.service;

import com.example.studentadmission.entity.Course;
import com.example.studentadmission.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // Task 3: Fetch Courses for specific Institute
    public List<Course> getCoursesByInstituteId(Long instituteId) {
        return courseRepository.findByInstituteInstituteId(instituteId);
    }

    // Optional: Add a course (Helper for testing)
    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }
}
