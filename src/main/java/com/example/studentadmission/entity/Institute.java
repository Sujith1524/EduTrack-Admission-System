package com.example.studentadmission.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "institute")
@Data
@NoArgsConstructor
public class Institute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long instituteId;

    @NotBlank(message = "Institute Name is Required")
    private String instituteName;

    @NotBlank(message = "Address is Required")
    private String address;

    @NotBlank(message = "Contact number is Required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String contactNo;
}