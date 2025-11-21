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
@RequestMapping("/institutes")
public class InstituteController {

    @Autowired
    private InstituteService instituteService;

    // Fetch Institute List
    @GetMapping
    public ResponseEntity<List<Institute>> fetchInstituteList() {
        List<Institute> institutes = instituteService.getAllInstitutes();

        if (institutes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(institutes);
    }
}
