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

    // Task: Take Admission (Logic to calculate dates)
    public Admission takeAdmission(Admission admission) {
        // 1. Fetch the FULL objects from the database using the IDs sent in the request
        Student student = studentRepository.findById(admission.getStudent().getStudentId()).orElse(null);
        Institute institute = instituteRepository.findById(admission.getInstitute().getInstituteId()).orElse(null);
        Course course = courseRepository.findById(admission.getCourse().getCourseId()).orElse(null);

        // 2. Replace the "Empty" objects in 'admission' with the "Full" objects found in DB
        // This ensures the response JSON has names, emails, etc., not just IDs.
        if (student != null) admission.setStudent(student);
        if (institute != null) admission.setInstitute(institute);

        if (course != null) {
            admission.setCourse(course); // Set the full course object

            // 3. Calculate Dates
            admission.setAdmissionDate(LocalDate.now());
            LocalDate endDate = LocalDate.now().plusDays(course.getDurationDays());
            admission.setCompletionDate(endDate);
        }

        // 4. Save and Return
        return admissionRepository.save(admission);
    }

    // Task: Count Students per Course
    public long getStudentCountByCourse(Long courseId) {
        return admissionRepository.countByCourseCourseId(courseId);
    }

    // Task: Duration Left for Course
    public String getDurationLeft(Long admissionId) {
        Admission admission = admissionRepository.findById(admissionId).orElse(null);
        if (admission == null) return "Admission not found";

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), admission.getCompletionDate());

        if (daysLeft < 0) return "Course Completed";
        return daysLeft + " days remaining";
    }

    // Task: Search Student Admission Details
    public List<Admission> getAdmissionsByStudent(Long studentId) {
        return admissionRepository.findByStudentStudentId(studentId);
    }
}
