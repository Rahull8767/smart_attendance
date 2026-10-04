package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID userId;
    private UUID providerId;
    private String employeeCode;
    private String name;
    private String department;
    private String designation;
    private String status;
    
    // Using a direct association for simplicity in MVP phase
    private UUID assignedSiteId;
    private UUID assignedShiftId;
}
