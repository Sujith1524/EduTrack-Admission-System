package com.example.studentadmission.controller;

import com.example.studentadmission.dto.CourseResponseDTO;
import com.example.studentadmission.dto.InstituteCoursesResponseDTO;
import com.example.studentadmission.entity.Course;
import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.service.CourseService;
import com.example.studentadmission.service.InstituteService;
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

    @Autowired
    private InstituteService instituteService;

    /**
     * Helper method to map Course entity to CourseResponseDTO
     */
    private CourseResponseDTO mapToCourseDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();

        // Course entities still show their own path
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

    /**
     * Helper method to map Institute entity and Course list to InstituteCoursesResponseDTO
     */
    private InstituteCoursesResponseDTO mapToInstituteCoursesDTO(Institute institute, List<Course> courses) {
        InstituteCoursesResponseDTO dto = new InstituteCoursesResponseDTO();

        // FIX: Set the _class property to the Institute Entity's class name
        dto.set_class(institute.get_class());

        dto.setInstituteId(institute.getInstituteId());
        dto.setInstituteName(institute.getInstituteName());
        dto.setInstituteEmail(institute.getInstituteEmail());
        dto.setInstitutePhone(institute.getInstitutePhone());
        dto.setInstituteAddress(institute.getInstituteAddress());

        // Conditional logic based on whether courses were found
        if (courses.isEmpty()) {
            dto.setCourseStatusMessage("No courses currently registered under this institute.");
        } else {
            List<CourseResponseDTO> courseDtos = courses.stream()
                    .map(this::mapToCourseDTO)
                    .collect(Collectors.toList());

            dto.setCourseStatusMessage("Course list retrieved successfully.");
            dto.setCourses(courseDtos);
        }

        return dto;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCourse(@Valid @RequestBody Course course) {
        Course savedCourse = courseService.addCourse(course);
        return ResponseUtil.success(mapToCourseDTO(savedCourse), "Course added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable("id") Long id, @Valid @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(id, course);
        return ResponseUtil.success(mapToCourseDTO(updatedCourse), "Course updated successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        List<CourseResponseDTO> dtos = courses.stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
        return ResponseUtil.success(dtos, "Courses fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable("id") Long id) {
        Course course = courseService.getCourseById(id);
        return ResponseUtil.success(mapToCourseDTO(course), "Course fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/institute/{instituteId}")
    public ResponseEntity<Map<String, Object>> getCoursesByInstituteId(@PathVariable("instituteId") Long instituteId) {
        // Fetch Institute details first (will throw 404 if not found)
        Institute institute = instituteService.getInstituteById(instituteId);

        // Fetch the list of courses (may be empty)
        List<Course> courses = courseService.getCoursesByInstituteId(instituteId);

        // Map to the new wrapper DTO with conditional messaging
        InstituteCoursesResponseDTO responseDto = mapToInstituteCoursesDTO(institute, courses);

        return ResponseUtil.success(responseDto, "Institute and course details retrieved", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return ResponseUtil.success(null, "Course deleted successfully", HttpStatus.OK);
    }
}