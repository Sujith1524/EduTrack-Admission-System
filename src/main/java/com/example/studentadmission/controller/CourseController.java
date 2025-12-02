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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * Helper method to map Course entity to CourseResponseDTO
     */
    private CourseResponseDTO mapToDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();

        // FIX: Set the _class property to the Entity's class name
        dto.set_class(course.get_class());

        dto.setCourseId(course.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setDurationDays(course.getDurationDays());
        dto.setFees(course.getFees());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());

        // Map nested Institute details
        if (course.getInstitute() != null) {
            dto.setInstituteId(course.getInstitute().getInstituteId());
            dto.setInstituteName(course.getInstitute().getInstituteName());
        }
        return dto;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCourse(@Valid @RequestBody Course course) {
        Course savedCourse = courseService.addCourse(course);
        return ResponseUtil.success(mapToDTO(savedCourse), "Course added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable("id") Long id, @Valid @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(id, course);
        return ResponseUtil.success(mapToDTO(updatedCourse), "Course updated successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        List<CourseResponseDTO> dtos = courses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseUtil.success(dtos, "Courses fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable("id") Long id) {
        Course course = courseService.getCourseById(id);
        return ResponseUtil.success(mapToDTO(course), "Course fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/institute/{instituteId}")
    public ResponseEntity<Map<String, Object>> getCoursesByInstituteId(@PathVariable("instituteId") Long instituteId) {
        List<Course> courses = courseService.getCoursesByInstituteId(instituteId);
        List<CourseResponseDTO> dtos = courses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseUtil.success(dtos, "Courses by institute fetched successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return ResponseUtil.success(null, "Course deleted successfully", HttpStatus.OK);
    }
}