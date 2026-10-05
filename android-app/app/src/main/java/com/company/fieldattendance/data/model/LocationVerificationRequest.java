package com.company.fieldattendance.data.model;

import java.util.UUID;

public class LocationVerificationRequest {
    public UUID employeeId;
    public double latitude;
    public double longitude;
    public float accuracy;
    public boolean isMockLocation;
    public Double altitude;

    public LocationVerificationRequest(UUID employeeId, double latitude, double longitude, float accuracy, boolean isMockLocation) {
        this(employeeId, latitude, longitude, accuracy, isMockLocation, 0.0);
    }

    public LocationVerificationRequest(UUID employeeId, double latitude, double longitude, float accuracy, boolean isMockLocation, Double altitude) {
        this.employeeId = employeeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.isMockLocation = isMockLocation;
        this.altitude = altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }
}
