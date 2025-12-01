package com.example.studentadmission.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder; // Import added
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@JsonPropertyOrder({
        "_class", "houseName", "street", "post", "city", "state", "pinCode"
}) // FIX: Explicitly set the JSON field order
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    // Requirement: _class field (placed first via @JsonPropertyOrder)
    @JsonProperty("_class")
    public String get_class() {
        return this.getClass().getName();
    }
}