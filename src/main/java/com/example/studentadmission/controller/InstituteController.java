package com.example.studentadmission.controller;

import com.example.studentadmission.dto.InstituteResponseDTO;
import com.example.studentadmission.entity.Institute;
import com.example.studentadmission.service.InstituteService;
import com.example.studentadmission.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/institutes")
public class InstituteController {

    @Autowired
    private InstituteService instituteService;

    /**
     * Helper method to map Institute entity to InstituteResponseDTO
     */
    private InstituteResponseDTO mapToDTO(Institute institute) {
        InstituteResponseDTO dto = new InstituteResponseDTO();
        dto.setInstituteId(institute.getInstituteId());
        dto.setInstituteName(institute.getInstituteName());
        dto.setInstituteEmail(institute.getInstituteEmail());
        dto.setInstitutePhone(institute.getInstitutePhone());
        dto.setInstituteAddress(institute.getInstituteAddress());
        dto.setCreatedAt(institute.getCreatedAt());
        dto.setUpdatedAt(institute.getUpdatedAt());
        return dto;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addInstitute(@Valid @RequestBody Institute institute) {
        Institute savedInstitute = instituteService.addInstitute(institute);
        return ResponseUtil.success(mapToDTO(savedInstitute), "Institute added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateInstitute(@PathVariable("id") Long id, @Valid @RequestBody Institute institute) {
        Institute updatedInstitute = instituteService.updateInstitute(id, institute);
        return ResponseUtil.success(mapToDTO(updatedInstitute), "Institute updated successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInstitutes() {
        List<Institute> institutes = instituteService.getAllInstitutes();
        List<InstituteResponseDTO> dtos = institutes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseUtil.success(dtos, "Institutes fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getInstituteById(@PathVariable("id") Long id) {
        Institute institute = instituteService.getInstituteById(id);
        return ResponseUtil.success(mapToDTO(institute), "Institute fetched successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteInstitute(@PathVariable("id") Long id) {
        instituteService.deleteInstitute(id);
        return ResponseUtil.success(null, "Institute deleted successfully", HttpStatus.OK);
    }
}