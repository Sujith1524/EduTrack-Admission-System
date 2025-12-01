package com.example.studentadmission.controller;

import com.example.studentadmission.dto.CourseResponseDTO;
import com.example.studentadmission.entity.Course;
import com.example.studentadmission.service.CourseService;
import com.example.studentadmission.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCourse(@Valid @RequestBody Course course) {
        CourseResponseDTO savedCourse = courseService.addCourse(course);
        return ResponseUtil.success(savedCourse, "Course added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {
        CourseResponseDTO updatedCourse = courseService.updateCourse(id, courseDetails);
        return ResponseUtil.success(updatedCourse, "Course updated successfully", HttpStatus.OK);
    }

    @GetMapping("/institute/{instituteId}")
    public ResponseEntity<Map<String, Object>> getCoursesByInstitute(@PathVariable Long instituteId) {
        Map<String, Object> data = courseService.getCoursesByInstituteId(instituteId);
        return ResponseUtil.success(data, "Courses fetched successfully", HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable("id") Long courseId) {
        Course deletedCourse = courseService.deleteCourse(courseId);
        return ResponseUtil.success(deletedCourse, "Course deleted successfully", HttpStatus.OK);
    }
}