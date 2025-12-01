package com.example.studentadmission.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Hide ID in response if you only want it internal
    private Long id;

    @NotBlank(message = "House Name is required")
    private String houseName;

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "Post Office is required")
    private String post;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pin Code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Pin Code must be 6 digits")
    private String pinCode;

    // --- Requirement: _class field ---
    @JsonProperty("_class")
    public String get_class() {
        return this.getClass().getName();
    }
}