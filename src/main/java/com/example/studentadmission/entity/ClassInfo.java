package com.example.studentadmission.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "class_info")
@Data
@NoArgsConstructor
public class ClassInfo {

    // Usually, the Class ID (e.g., "C105") is provided by the system,
    // but here we treat it as a unique identifier string.
    @Id
    @NotBlank(message = "Class ID is required")
    private String classId;

    @NotBlank(message = "Class Name is required")
    private String className;

    @NotBlank(message = "Section is required")
    private String section;

    @NotBlank(message = "Academic Year is required")
    private String academicYear;

    // --- Requirement: _class field ---
    @JsonProperty("_class")
    public String get_class() {
        return this.getClass().getName();
    }
}