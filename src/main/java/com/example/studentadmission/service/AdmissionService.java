package com.example.studentadmission.service;

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

    // Take Admission
    public Admission takeAdmission(Admission admission) {
        Student student = studentRepository.findById(admission.getStudent().getStudentId()).orElse(null);
        Institute institute = instituteRepository.findById(admission.getInstitute().getInstituteId()).orElse(null);
        Course course = courseRepository.findById(admission.getCourse().getCourseId()).orElse(null);

        if (institute != null) admission.setInstitute(institute);

        if (course != null) {
            admission.setCourse(course);

            admission.setAdmissionDate(LocalDate.now());
            LocalDate endDate = LocalDate.now().plusDays(course.getDurationDays());
            admission.setCompletionDate(endDate);
        }

        return admissionRepository.save(admission);
    }

    // Count Students per Course
    public long getStudentCountByCourse(Long courseId) {
        return admissionRepository.countByCourseCourseId(courseId);
    }

    // Duration Left for Course
    public String getDurationLeft(Long admissionId) {
        Admission admission = admissionRepository.findById(admissionId).orElse(null);
        if (admission == null) return "Admission not found";

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), admission.getCompletionDate());

        if (daysLeft < 0) return "Course Completed";
        return daysLeft + " days remaining";
    }

    // Search Student Admission Details
    public List<Admission> getAdmissionsByStudent(Long studentId) {
        return admissionRepository.findByStudentStudentId(studentId);
    }
}
