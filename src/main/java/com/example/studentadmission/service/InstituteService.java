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

    public Institute addInstitute(Institute institute) {
        // Validation Logic
        Optional<Institute> existingByName = instituteRepository.findByInstituteName(institute.getInstituteName());
        if (existingByName.isPresent()) {
            throw new RuntimeException("Institute with name '" + institute.getInstituteName() + "' already exists.");
        }

        if (instituteRepository.findByInstituteEmail(institute.getInstituteEmail()).isPresent()) {
            throw new RuntimeException("Institute with email '" + institute.getInstituteEmail() + "' already exists.");
        }

        Optional<Institute> existingByDetails = instituteRepository.findByInstituteAddressOrInstitutePhone(
                institute.getInstituteAddress(),
                institute.getInstitutePhone()
        );

        if (existingByDetails.isPresent()) {
            throw new RuntimeException("An institute with this address or phone number already exists.");
        }

        return instituteRepository.save(institute);
    }

    public Institute updateInstitute(Long instituteId, Institute updatedInstitute) {
        Institute existingInstitute = instituteRepository.findById(instituteId)
                .orElseThrow(() -> new RuntimeException("Institute not found with ID: " + instituteId));

        existingInstitute.setInstituteName(updatedInstitute.getInstituteName());
        existingInstitute.setInstituteEmail(updatedInstitute.getInstituteEmail());

        // Check if the updated email is already used by a DIFFERENT institute
        Optional<Institute> existingByEmail = instituteRepository.findByInstituteEmail(updatedInstitute.getInstituteEmail());
        if (existingByEmail.isPresent() && !existingByEmail.get().getInstituteId().equals(instituteId)) {
            throw new RuntimeException("Institute with email '" + updatedInstitute.getInstituteEmail() + "' already exists.");
        }

        existingInstitute.setInstitutePhone(updatedInstitute.getInstitutePhone());
        existingInstitute.setInstituteAddress(updatedInstitute.getInstituteAddress());

        return instituteRepository.save(existingInstitute);
    }

    public List<Institute> getAllInstitutes() {
        return instituteRepository.findAll();
    }

    public Institute getInstituteById(Long instituteId) {
        return instituteRepository.findById(instituteId)
                .orElseThrow(() -> new RuntimeException("Institute not found with ID: " + instituteId));
    }

    public void deleteInstitute(Long instituteId) {
        if (!instituteRepository.existsById(instituteId)) {
            throw new RuntimeException("Institute not found with ID: " + instituteId);
        }
        instituteRepository.deleteById(instituteId);
    }
}