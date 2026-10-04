package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "face_profiles")
public class FaceProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID employeeId;
    @Column(columnDefinition = "TEXT")
    private String faceEmbeddingJson; // Stores the 192-float array as JSON
    private String enrollmentStatus;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
