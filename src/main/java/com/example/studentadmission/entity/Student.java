package com.example.studentadmission.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // REQUIRED for Hibernate proxy fix
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder({
        "_class", "studentId", "studentName", "studentEmail", "role",
        "createdAt", "updatedAt"
})
// Ignore internal Hibernate proxy fields to prevent serialization errors
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Student {

    public enum Role {
        STUDENT, ADMIN, SUPER_ADMIN
    }

    public enum Gender { Male, Female, Other }
    public enum Status { ACTIVE, INACTIVE, SUSPENDED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long studentId;

    @NotBlank(message = "First Name is required")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Student Name is required")
    @Column(nullable = false, length = 100)
    private String studentName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false, length = 100)
    private String studentEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @NotNull(message = "Date of Birth is required")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.STUDENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) // FIX 2: Changed to EAGER
    @JoinColumn(name = "address_id")
    @Valid
    @NotNull(message = "Address information is required")
    private Address address;

    @ManyToOne(fetch = FetchType.EAGER) // FIX 2: Changed to EAGER
    @JoinColumn(name = "class_info_id")
    @Valid
    @NotNull(message = "Class information is required")
    private ClassInfo classInfo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonProperty("_class")
    public String get_class() {
        return this.getClass().getName();
    }
}