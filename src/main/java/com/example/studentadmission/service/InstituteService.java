package com.example.studentadmission.service;

import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.repository.InstituteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InstituteService {

    @Autowired
    private InstituteRepository instituteRepository;

    /**
     * Task: Fetch Institute List (READ operation)
     * Fetches all institutes from the database.
     * @return A list of all Institute entities.
     */
    public List<Institute> getAllInstitutes() {
        return instituteRepository.findAll();
    }
}
