package com.example.studentadmission.repository;

import com.example.studentadmission.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassInfoRepository extends JpaRepository<ClassInfo, String> {
    // Looks up by the manually assigned String ID (e.g., C105)
}