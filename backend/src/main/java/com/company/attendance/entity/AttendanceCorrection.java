package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attendance_corrections")
public class AttendanceCorrection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID attendanceId; // Optional: Can be null if missing a punch-in completely
    private UUID employeeId;
    
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED
    
    @Column(name = "requested_at", updatable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();
    
    private UUID reviewedBy;
    private LocalDateTime reviewedAt;
    private String managerComment;
}
