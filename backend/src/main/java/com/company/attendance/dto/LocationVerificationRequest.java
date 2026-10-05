package com.company.attendance.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationVerificationRequest {
    private UUID employeeId;
    private Double latitude;
    private Double longitude;
    private Float accuracy;
    private Boolean isMockLocation;
    private Double altitude;
}
