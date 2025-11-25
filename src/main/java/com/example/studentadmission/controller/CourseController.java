package com.example.studentadmission.controller;

import com.example.studentadmission.dto.CourseResponseDTO;
import com.example.studentadmission.entity.Course;
import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.service.CourseService;
import com.example.studentadmission.service.InstituteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private InstituteService instituteService;

    // Helper method to convert Course Entity to DTO (needed for structured output)
    private CourseResponseDTO convertToDto(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setDurationDays(course.getDurationDays());
        return dto;
    }

    private Map<String, Object> createErrorResponse(BindingResult result) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("package", this.getClass().getPackageName());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        errorResponse.put("message", "Validation Failed: Missing or Invalid Course Input");
        errorResponse.put("status", "Error");
        errorResponse.put("errors", fieldErrors);

        return errorResponse;
    }

    // --- 1. ADD COURSE (Structured and Validated - Converted to DTO for clean output) ---
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCourse(@Valid @RequestBody Course course, BindingResult result) {

        if (result.hasErrors()) {
            return new ResponseEntity<>(createErrorResponse(result), HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            Course savedCourse = courseService.addCourse(course);
            CourseResponseDTO savedCourseDto = convertToDto(savedCourse); // Convert to DTO here!

            response.put("package", this.getClass().getPackageName());
            response.put("message", "Course added successfully");
            response.put("status", "Success");
            response.put("data", savedCourseDto); // Return the clean DTO

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            response.put("package", this.getClass().getPackageName());
            response.put("message", e.getMessage());
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // --- 2. UPDATE COURSE (Structured - FIX APPLIED HERE) ---
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        try {
            Course updatedCourse = courseService.updateCourse(id, courseDetails);

            if (updatedCourse != null) {
                // FIX: Convert the updated entity to the DTO before sending it in the response
                CourseResponseDTO updatedCourseDto = convertToDto(updatedCourse);

                response.put("message", "Course updated successfully");
                response.put("status", "Success");
                response.put("data", updatedCourseDto); // Return the clean DTO
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Course with ID " + id + " not found");
                response.put("status", "Error");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // --- 3. FETCH COURSES FOR INSTITUTE (Structured and uses DTOs) ---
    @GetMapping("/institute/{instituteId}")
    public ResponseEntity<Map<String, Object>> getCoursesByInstitute(@PathVariable Long instituteId) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("package", this.getClass().getPackageName());

        Institute instituteDetails = instituteService.getInstituteDetailsById(instituteId);

        if (instituteDetails == null) {
            response.put("message", "Institute with ID " + instituteId + " not found.");
            response.put("status", "Error");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Service already returns List<CourseResponseDTO>
        List<CourseResponseDTO> courses = courseService.getCoursesByInstituteId(instituteId);

        Map<String, Object> instituteHeader = new LinkedHashMap<>();
        instituteHeader.put("instituteId", instituteDetails.getInstituteId());
        instituteHeader.put("instituteName", instituteDetails.getInstituteName());
        instituteHeader.put("address", instituteDetails.getAddress());
        instituteHeader.put("contactNo", instituteDetails.getContactNo());

        response.put("message", "Courses fetched successfully for " + instituteDetails.getInstituteName());
        response.put("status", "Success");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("instituteDetails", instituteHeader);
        data.put("courses", courses);

        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
