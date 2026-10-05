package com.company.attendance.dto;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class CeoResponse {
    private UUID id;
    private String name;
    private String companyName;
    private String status;
    private long employeeCount;
    private long siteCount;
    private LocalDateTime lastActive;
}
