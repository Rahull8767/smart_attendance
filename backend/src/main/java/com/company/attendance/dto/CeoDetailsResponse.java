package com.company.attendance.dto;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class CeoDetailsResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String designation;
    private String status;
    private LocalDateTime createdAt;
    
    // Org Info
    private UUID organizationId;
    private String companyName;
    private String industry;
    private String companyEmail;
    private String companyPhone;
    
    // Stats
    private long employeeCount;
    private long siteCount;
}
