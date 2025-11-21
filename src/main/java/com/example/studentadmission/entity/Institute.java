package com.example.studentadmission.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "institute")
@Data
public class Institute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long instituteId;

    private String instituteName;
    private String address;
    private String contactNo;
}