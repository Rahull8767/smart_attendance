package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "biometric_machine_logs")
public class BiometricMachineLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String machineSerialNo;
    private String enrollmentId; // ID from the machine
    private String punchState;
    private LocalDateTime punchTime;
    
    private Boolean processed = false;
    
    @Column(name = "received_at", updatable = false)
    private LocalDateTime receivedAt = LocalDateTime.now();
}
