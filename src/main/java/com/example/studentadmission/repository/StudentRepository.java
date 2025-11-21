package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// JpaRepository provides methods like save(), findById(), findAll(), delete()
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Spring Data JPA automatically generates the SQL for this method name
    Student findByEmail(String email);
}