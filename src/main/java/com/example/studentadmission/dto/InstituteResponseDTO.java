package com.example.studentadmission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
        "_class", "instituteId", "instituteName", "instituteEmail",
        "institutePhone", "instituteAddress", "createdAt", "updatedAt"
})
public class InstituteResponseDTO {

    // FIX: Custom getter for _class property
    @JsonProperty("_class")
    public String get_class() {
        return "com.example.studentadmission.entity.Institute";
    }

    private Long instituteId;
    private String instituteName;
    private String instituteEmail;
    private String institutePhone;
    private String instituteAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}