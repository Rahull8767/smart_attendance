package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ceos")
public class Ceo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID userId;
    private UUID providerId;
    private String name;
    private String email;
    private String phone;
    private String designation;
    private String status;
    private UUID organizationId;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
