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
        return instituteRepository.findById(instituteId).orElse(null);
    }

    public Institute addInstitute(Institute institute) {
        return instituteRepository.save(institute);
    }

    public Institute updateInstitute(Long id, Institute instituteDetails) {
        Optional<Institute> instituteOptional = instituteRepository.findById(id);

        if (instituteOptional.isPresent()) {
            Institute existingInstitute = instituteOptional.get();

            if (instituteDetails.getInstituteName() != null)
                existingInstitute.setInstituteName(instituteDetails.getInstituteName());

            if (instituteDetails.getAddress() != null)
                existingInstitute.setAddress(instituteDetails.getAddress());

            if (instituteDetails.getContactNo() != null)
                existingInstitute.setContactNo(instituteDetails.getContactNo());

            return instituteRepository.save(existingInstitute);
        } else {
            return null;
        }
    }
}