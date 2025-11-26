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

    public Admission takeAdmission(Admission admission) {
        // 1. Fetch the FULL objects from the database to avoid Null values in response
        Student student = studentRepository.findById(admission.getStudent().getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Institute institute = instituteRepository.findById(admission.getInstitute().getInstituteId())
                .orElseThrow(() -> new RuntimeException("Institute not found"));

        Course course = courseRepository.findById(admission.getCourse().getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 2. VALIDATION: Check if the Course actually belongs to the selected Institute
        // This prevents the error where Course 1 (Institute A) is assigned to Institute B
        if (!course.getInstitute().getInstituteId().equals(institute.getInstituteId())) {
            throw new RuntimeException("Course '" + course.getCourseName() + "' is not offered by " + institute.getInstituteName());
        }

        // 3. Set the Full Objects into the Admission entity
        admission.setStudent(student);
        admission.setInstitute(institute);
        admission.setCourse(course);

        // 4. Calculate Dates
        admission.setAdmissionDate(LocalDate.now());
        LocalDate endDate = LocalDate.now().plusDays(course.getDurationDays());
        admission.setCompletionDate(endDate);

        return admissionRepository.save(admission);
    }

    public long getStudentCountByCourse(Long courseId) {
        return admissionRepository.countByCourseCourseId(courseId);
    }

    public String getDurationLeft(Long admissionId) {
        Admission admission = admissionRepository.findById(admissionId).orElse(null);
        if (admission == null) return "Admission not found";

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), admission.getCompletionDate());

        if (daysLeft < 0) return "Course Completed";
        return daysLeft + " days remaining";
    }

    public List<Admission> getAdmissionsByStudent(Long studentId) {
        return admissionRepository.findByStudentStudentId(studentId);
    }
}