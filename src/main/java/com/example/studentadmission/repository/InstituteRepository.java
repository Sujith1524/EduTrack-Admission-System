package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, Long> {
    // JpaRepository provides findAll() for fetching the list automatically.
}
