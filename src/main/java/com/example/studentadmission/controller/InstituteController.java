package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/institutes") // Base URL: http://localhost:8080/api/institutes
public class InstituteController {

    @Autowired
    private InstituteService instituteService;

    /**
     * Endpoint for Task 3: Fetch Institute List
     * Handles GET request to retrieve all institutes.
     * @return A list of institutes with HTTP status 200 OK.
     */
    @GetMapping // Maps to /api/institutes
    public ResponseEntity<List<Institute>> fetchInstituteList() {
        List<Institute> institutes = instituteService.getAllInstitutes();

        if (institutes.isEmpty()) {
            // Return 204 No Content if the list is empty
            return ResponseEntity.noContent().build();
        }
        // Return 200 OK with the list of institutes
        return ResponseEntity.ok(institutes);
    }
}
