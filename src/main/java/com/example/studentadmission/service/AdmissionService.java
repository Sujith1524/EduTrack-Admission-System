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
        Student student = studentRepository.findById(admission.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Institute institute = instituteRepository.findById(admission.getInstitute().getInstituteId())
                .orElseThrow(() -> new RuntimeException("Institute not found"));
        Course course = courseRepository.findById(admission.getCourse().getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstitute().getInstituteId().equals(institute.getInstituteId())) {
            throw new RuntimeException("Course '" + course.getCourseName() + "' is not offered by " + institute.getInstituteName());
        }

        admission.setStudent(student);
        admission.setInstitute(institute);
        admission.setCourse(course);
        admission.setAdmissionDate(LocalDate.now());
        admission.setCompletionDate(LocalDate.now().plusDays(course.getDurationDays()));

        Admission savedAdmission = admissionRepository.save(admission);
        return AdmissionResponse.fromEntity(savedAdmission);
    }

    public List<AdmissionResponse> getAdmissionsByStudent(Long studentId) {
        List<Admission> admissions = admissionRepository.findByStudentStudentId(studentId);
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