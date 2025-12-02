package com.example.studentadmission.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@JsonPropertyOrder({
        "_class", "courseId", "courseName", "institute", "durationDays",
        "fees", "createdAt", "updatedAt"
})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @NotBlank(message = "Course Name is required")
    private String courseName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institute_id")
    @NotNull(message = "Institute is required")
    private Institute institute;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private int durationDays;

    @NotNull(message = "Fees are required")
    @Min(value = 0, message = "Fees cannot be negative")
    private BigDecimal fees;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // FIX: Custom getter for _class property to show the ENTITY class name
    @JsonProperty("_class")
    public String get_class() {
        return this.getClass().getName();
    }
}