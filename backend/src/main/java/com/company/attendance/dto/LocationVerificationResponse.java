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
    private double allowedRadius;
    private Double siteLatitude;
    private Double siteLongitude;
    private Double siteAltitude;
    private Double altitudeDifference;
    private String siteName;

    public LocationVerificationResponse(boolean verified, String status, String message, double calculatedDistance) {
        this.verified = verified;
        this.status = status;
        this.message = message;
        this.calculatedDistance = calculatedDistance;
    }
}
