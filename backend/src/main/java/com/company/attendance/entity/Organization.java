package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String name;
    private String industry;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
