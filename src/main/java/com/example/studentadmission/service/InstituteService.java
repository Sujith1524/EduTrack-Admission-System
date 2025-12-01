package com.example.studentadmission.service;

import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.repository.InstituteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstituteService {

    @Autowired
    private InstituteRepository instituteRepository;

    public List<Institute> getAllInstitutes() {
        return instituteRepository.findAll();
    }

    public Institute getInstituteDetailsById(Long instituteId) {
        return instituteRepository.findById(instituteId)
                .orElseThrow(() -> new RuntimeException("Institute with ID " + instituteId + " not found."));
    }

    public Institute addInstitute(Institute institute) {
        return instituteRepository.save(institute);
    }

    public Institute updateInstitute(Long id, Institute instituteDetails) {
        Institute existingInstitute = instituteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institute with ID " + id + " not found"));

        if (instituteDetails.getInstituteName() != null) existingInstitute.setInstituteName(instituteDetails.getInstituteName());
        if (instituteDetails.getAddress() != null) existingInstitute.setAddress(instituteDetails.getAddress());
        if (instituteDetails.getContactNo() != null) existingInstitute.setContactNo(instituteDetails.getContactNo());

        return instituteRepository.save(existingInstitute);
    }

    public Institute deleteInstitute(Long instituteId) {
        Institute instituteToDelete = getInstituteDetailsById(instituteId);
        instituteRepository.delete(instituteToDelete);
        return instituteToDelete;
    }
}