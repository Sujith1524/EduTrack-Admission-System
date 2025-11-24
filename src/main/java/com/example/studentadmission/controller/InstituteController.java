package com.example.studentadmission.controller;

import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.repository.InstituteRepository;
import com.example.studentadmission.response.ApiResponse;
import com.example.studentadmission.service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/institutes")
public class InstituteController {

    @Autowired
    private InstituteService instituteService;

    @Autowired
    private InstituteRepository instituteRepository;

    @GetMapping("")
    public ResponseEntity<ApiResponse> getAllInstitutes() {

        List<Institute> list = instituteService.getAllInstitutes();

        return ResponseEntity.ok(
                new ApiResponse(
                        "com.example.java_crud.controller",
                        "success",
                        "Institute list fetched successfully",
                        list
                )
        );
    }

    // ADD
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addInstitute(@RequestBody Institute institute) {

        String validation = instituteService.validateInstitute(institute);

        if (!validation.equals("OK")) {
            return ResponseEntity.ok(
                    new ApiResponse(
                            "com.example.java_crud.controller",
                            "error",
                            validation,
                            null
                    )
            );
        }

        Institute saved = instituteService.addInstitute(institute);

        return ResponseEntity.ok(
                new ApiResponse(
                        "com.example.java_crud.controller",
                        "success",
                        "Institute added successfully",
                        saved
                )
        );
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateInstitute(@PathVariable Long id, @RequestBody Institute institute) {

        Institute existing = instituteService.updateInstitute(id, institute);

        if (existing == null) {
            return ResponseEntity.ok(
                    new ApiResponse(
                            "com.example.java_crud.controller",
                            "error",
                            "Institute ID not found",
                            null
                    )
            );
        }

        return ResponseEntity.ok(
                new ApiResponse(
                        "com.example.java_crud.controller",
                        "success",
                        "Institute updated successfully",
                        existing
                )
        );
    }
}

