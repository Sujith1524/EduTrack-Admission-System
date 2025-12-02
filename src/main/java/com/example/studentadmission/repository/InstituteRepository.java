package com.example.studentadmission.repository;

import com.example.studentadmission.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, Long> {

    // Finds institute by name (Required for Service validation)
    Optional<Institute> findByInstituteName(String instituteName);

    // Finds institute by email (Required for Service validation)
    Optional<Institute> findByInstituteEmail(String instituteEmail);

    // Finds institute by address OR phone (Required for Service validation)
    Optional<Institute> findByInstituteAddressOrInstitutePhone(String instituteAddress, String institutePhone);
}