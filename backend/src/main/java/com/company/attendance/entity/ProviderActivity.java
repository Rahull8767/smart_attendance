package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "provider_activity")
public class ProviderActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID providerId;
    private String eventType;
    private String entityType;
    private UUID entityId;
    private String title;
    private String description;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
