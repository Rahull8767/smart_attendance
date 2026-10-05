package com.company.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private java.util.UUID userId;
    private String role;
    private java.util.UUID providerId;
    private java.util.UUID ceoId;
    private java.util.UUID employeeId;
    private String displayName;
}
