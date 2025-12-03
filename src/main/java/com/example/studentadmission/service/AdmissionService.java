package com.example.studentadmission.service;

import com.example.studentadmission.dto.AdmissionResponse;
import com.example.studentadmission.entity.Admission;
import com.example.studentadmission.entity.Course;
import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.entity.Student;
import com.example.studentadmission.repository.AdmissionRepository;
import com.example.studentadmission.repository.CourseRepository;
import com.example.studentadmission.repository.InstituteRepository;
import com.example.studentadmission.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdmissionService {

    @Autowired
    private AdmissionRepository admissionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private InstituteRepository instituteRepository;

    public AdmissionResponse takeAdmission(Admission admission) {

        // 1. Validate & Fetch Student
        if (admission.getStudent() == null || admission.getStudent().getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        Student student = studentRepository.findById(admission.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + admission.getStudent().getStudentId()));

        // 2. Validate & Fetch Institute
        if (admission.getInstitute() == null || admission.getInstitute().getInstituteId() == null) {
            throw new IllegalArgumentException("Institute ID is required.");
        }
        Institute institute = instituteRepository.findById(admission.getInstitute().getInstituteId())
                .orElseThrow(() -> new RuntimeException("Institute not found with ID: " + admission.getInstitute().getInstituteId()));

        // 3. Validate & Fetch Course
        if (admission.getCourse() == null || admission.getCourse().getCourseId() == null) {
            throw new IllegalArgumentException("Course ID is required.");
        }
        Course course = courseRepository.findById(admission.getCourse().getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + admission.getCourse().getCourseId()));

        // 4. CRITICAL CHECK: Does this Course belong to this Institute?
        if (!course.getInstitute().getInstituteId().equals(institute.getInstituteId())) {
            throw new RuntimeException("Invalid Request: The course '" + course.getCourseName() +
                    "' is not offered by the institute '" + institute.getInstituteName() + "'.");
        }

        // 5. Set the fetched full objects into the Admission entity
        admission.setStudent(student);
        admission.setInstitute(institute);
        admission.setCourse(course);

        // 6. Set calculated fields
        admission.setAdmissionDate(LocalDate.now());
        admission.setCompletionDate(LocalDate.now().plusDays(course.getDurationDays()));

        // 7. Save
        Admission savedAdmission = admissionRepository.save(admission);
        return AdmissionResponse.fromEntity(savedAdmission);
    }

    @Transactional(readOnly = true)
    public List<AdmissionResponse> getAdmissionsByStudent(Long studentId) {
        List<Admission> admissions = admissionRepository.findByStudent_StudentId(studentId);

        // FIX: Manually initialize Institute and Course within the Admissions list
        // to ensure they are fully loaded before mapping to the DTO and serialization.
        admissions.forEach(admission -> {
            // Note: Student is likely loaded EAGERLY, but Institute and Course are often LAZY.
            Hibernate.initialize(admission.getInstitute());
            Hibernate.initialize(admission.getCourse());
        });

        return admissions.stream()
                .map(AdmissionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public String getDurationLeft(Long admissionId) {
        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), admission.getCompletionDate());
        return (daysLeft < 0) ? "Course Completed" : daysLeft + " days remaining";
    }
}