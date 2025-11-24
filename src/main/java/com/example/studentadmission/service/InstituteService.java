package com.example.studentadmission.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.studentadmission.repository.InstituteRepository;
import com.example.studentadmission.entity.Institute;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstituteService {

    @Autowired
    private InstituteRepository instituteRepository;

    // ADD INSTITUTE
    public String validateInstitute(Institute institute) {

        if (institute.getInstituteName() == null || institute.getInstituteName().isEmpty()) {
            return "Institute name is required";
        }

        if (institute.getAddress() == null || institute.getAddress().isEmpty()) {
            return "Address is required";
        }

        if (institute.getContactNo() == null || institute.getContactNo().isEmpty()) {
            return "Contact number is required";
        }

        if (!institute.getContactNo().matches("\\d{10}")) {
            return "Contact number must be 10 digits";
        }

        return "OK";
    }


    // Add
    public Institute addInstitute(Institute institute) {
        return instituteRepository.save(institute);
    }

    // Update
    public Institute updateInstitute(Long id, Institute institute) {

        Institute existing = instituteRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        if (institute.getInstituteName() != null) {
            existing.setInstituteName(institute.getInstituteName());
        }

        if (institute.getAddress() != null) {
            existing.setAddress(institute.getAddress());
        }

        if (institute.getContactNo() != null && institute.getContactNo().matches("\\d{10}")) {
            existing.setContactNo(institute.getContactNo());
        }

        return instituteRepository.save(existing);
    }

    // Fetching all details of Institute
    public List<Institute> getAllInstitutes() {
        return instituteRepository.findAll();
    }

}
