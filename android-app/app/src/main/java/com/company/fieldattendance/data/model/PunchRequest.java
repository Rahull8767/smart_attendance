package com.company.fieldattendance.data.model;

import java.util.UUID;
import java.util.List;

public class PunchRequest {
    public UUID employeeId;
    public Double latitude;
    public Double longitude;
    public Float accuracy;
    public Double altitude;
    public String faceStatus;
    public List<Float> faceEmbedding;
    
    public PunchRequest(UUID employeeId, Double latitude, Double longitude, Float accuracy, String faceStatus) {
        this(employeeId, latitude, longitude, accuracy, 0.0, faceStatus);
    }

    public PunchRequest(UUID employeeId, Double latitude, Double longitude, Float accuracy, Double altitude, String faceStatus) {
        this.employeeId = employeeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.faceStatus = faceStatus;
    }
    
    public void setFaceEmbedding(List<Float> faceEmbedding) {
        this.faceEmbedding = faceEmbedding;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }
}
