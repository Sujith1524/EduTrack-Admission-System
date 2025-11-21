package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Course;
import com.example.studentadmission.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // Fetch Courses for Institute
    @GetMapping("/institute/{instituteId}")
    public ResponseEntity<List<Course>> getCoursesByInstitute(@PathVariable Long instituteId) {
        List<Course> courses = courseService.getCoursesByInstituteId(instituteId);

        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(courses);
    }
}
