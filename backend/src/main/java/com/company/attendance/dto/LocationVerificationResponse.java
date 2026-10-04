package com.company.attendance.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationVerificationResponse {
    private boolean verified;
    private String status;
    private String message;
    private double calculatedDistance;
}
