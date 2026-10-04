package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String action;
    private String username;
    private String details;
    private String ipAddress;
    
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
