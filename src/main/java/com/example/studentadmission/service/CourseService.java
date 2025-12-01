package com.example.studentadmission.service;

import com.example.studentadmission.dto.CourseResponseDTO;
import com.example.studentadmission.entity.Course;
import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.repository.CourseRepository;
import com.example.studentadmission.repository.InstituteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstituteRepository instituteRepository;

    // Helper: Convert Entity to DTO
    private CourseResponseDTO convertToDto(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setDurationDays(course.getDurationDays());
        return dto;
    }

    public Map<String, Object> getCoursesByInstituteId(Long instituteId) {
        Institute institute = instituteRepository.findById(instituteId)
                .orElseThrow(() -> new RuntimeException("Institute with ID " + instituteId + " not found."));

        List<CourseResponseDTO> courses = courseRepository.findByInstituteInstituteId(instituteId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        Map<String, Object> instituteHeader = new LinkedHashMap<>();
        instituteHeader.put("instituteId", institute.getInstituteId());
        instituteHeader.put("instituteName", institute.getInstituteName());
        instituteHeader.put("address", institute.getAddress());
        instituteHeader.put("contactNo", institute.getContactNo());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("instituteDetails", instituteHeader);
        result.put("courses", courses);
        return result;
    }

    public CourseResponseDTO addCourse(Course course) {
        if (course.getInstitute() == null || course.getInstitute().getInstituteId() == null) {
            throw new RuntimeException("Institute ID is missing.");
        }
        Institute institute = instituteRepository.findById(course.getInstitute().getInstituteId())
                .orElseThrow(() -> new RuntimeException("Institute with ID " + course.getInstitute().getInstituteId() + " not found."));

        course.setInstitute(institute);
        Course savedCourse = courseRepository.save(course);
        return convertToDto(savedCourse);
    }

    public CourseResponseDTO updateCourse(Long id, Course courseDetails) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course with ID " + id + " not found"));

        if (courseDetails.getCourseName() != null) existingCourse.setCourseName(courseDetails.getCourseName());
        if (courseDetails.getDurationDays() != null) existingCourse.setDurationDays(courseDetails.getDurationDays());

        if (courseDetails.getInstitute() != null && courseDetails.getInstitute().getInstituteId() != null) {
            Institute newInstitute = instituteRepository.findById(courseDetails.getInstitute().getInstituteId())
                    .orElseThrow(() -> new RuntimeException("Cannot update: Institute ID not found."));
            existingCourse.setInstitute(newInstitute);
        }

        return convertToDto(courseRepository.save(existingCourse));
    }

    public Course deleteCourse(Long courseId) {
        Course courseToDelete = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        courseRepository.deleteById(courseId);
        courseToDelete.setInstitute(null); // Avoid serialization issues
        return courseToDelete;
    }
}