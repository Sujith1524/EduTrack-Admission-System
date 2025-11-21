package com.example.studentadmission.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "student")
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    private String phone;

    private Timestamp createdAt;

    // ✅ ADD THIS BLOCK
    // This method runs automatically right before the data is saved to the DB
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
}