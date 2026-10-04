package com.company.attendance.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class LocationVerificationRequest {
    private UUID employeeId;
    private Double latitude;
    private Double longitude;
    private Float accuracy;
    private Boolean isMockLocation;
}
