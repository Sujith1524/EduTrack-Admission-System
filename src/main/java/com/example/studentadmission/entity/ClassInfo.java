package com.example.studentadmission.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder; // Import added
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "class_info")
@Data
@NoArgsConstructor
@JsonPropertyOrder({
        "_class", "classId", "className", "section", "academicYear"
}) // FIX: Explicitly set the JSON field order
public class ClassInfo {

    @Id
    @NotBlank(message = "Class ID is required")
    private String classId;

    @NotBlank(message = "Class Name is required")
    private String className;

    @NotBlank(message = "Section is required")
    private String section;

    @NotBlank(message = "Academic Year is required")
    private String academicYear;

    // Requirement: _class field (placed first via @JsonPropertyOrder)
    @JsonProperty("_class")
    public String get_class() {
        return this.getClass().getName();
    }
}