package com.example.studentadmission.service;

import com.example.studentadmission.dto.CourseResponseDTO;
import com.example.studentadmission.entity.Course;
import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.repository.CourseRepository;
import com.example.studentadmission.repository.InstituteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstituteRepository instituteRepository;

    private CourseResponseDTO convertToDto(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setDurationDays(course.getDurationDays());
        return dto;
    }

    public List<CourseResponseDTO> getCoursesByInstituteId(Long instituteId) {
        List<Course> courses = courseRepository.findByInstituteInstituteId(instituteId);

        return courses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Course addCourse(Course course) {
        if (course.getInstitute() == null || course.getInstitute().getInstituteId() == null) {
            throw new RuntimeException("Institute ID is missing.");
        }

        Optional<Institute> institute = instituteRepository.findById(course.getInstitute().getInstituteId());

        if (institute.isEmpty()) {
            throw new RuntimeException("Institute with ID " + course.getInstitute().getInstituteId() + " not found.");
        }

        course.setInstitute(institute.get());

        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Optional<Course> courseOptional = courseRepository.findById(id);

        if (courseOptional.isPresent()) {
            Course existingCourse = courseOptional.get();

            if (courseDetails.getCourseName() != null)
                existingCourse.setCourseName(courseDetails.getCourseName());

            if (courseDetails.getDurationDays() != null)
                existingCourse.setDurationDays(courseDetails.getDurationDays());

            if (courseDetails.getInstitute() != null && courseDetails.getInstitute().getInstituteId() != null) {
                Optional<Institute> newInstitute = instituteRepository.findById(courseDetails.getInstitute().getInstituteId());
                if (newInstitute.isEmpty()) {
                    throw new RuntimeException("Cannot update course: Institute ID " + courseDetails.getInstitute().getInstituteId() + " not found.");
                }
                existingCourse.setInstitute(newInstitute.get());
            }

            return courseRepository.save(existingCourse);
        } else {
            return null;
        }
    }
}