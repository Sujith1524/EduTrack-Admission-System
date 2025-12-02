package com.example.studentadmission.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "institute")
@Data
@NoArgsConstructor
@JsonPropertyOrder({
        "_class", "instituteId", "instituteName", "instituteEmail",
        "institutePhone", "instituteAddress", "createdAt", "updatedAt"
})
public class Institute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long instituteId;

    @NotBlank(message = "Institute Name is required")
    private String instituteName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String instituteEmail;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    @Column(name = "contact_no") // Keep mapping for contact_no since the generated SQL used it
    private String institutePhone;

    @NotBlank(message = "Address is required")
    // FIX: Removed @Column(name = "address"). This forces Hibernate to use the default 'institute_address'
    // which the database explicitly reported as the missing required field.
    private String instituteAddress;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Custom getter for _class property
    @JsonProperty("_class")
    public String get_class() {
        return this.getClass().getName();
    }
}