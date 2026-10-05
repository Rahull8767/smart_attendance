package com.company.attendance.dto;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class ProviderAlertResponse {
    private UUID id;
    private String title;
    private String description;
    private String severity; // e.g. HIGH, MEDIUM, LOW
    private LocalDateTime timestamp;
    private Boolean isRead;
}
