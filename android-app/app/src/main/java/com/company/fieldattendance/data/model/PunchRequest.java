package com.company.fieldattendance.data.model;

import java.util.UUID;

public class PunchRequest {
    public UUID employeeId;
    public Double latitude;
    public Double longitude;
    public Float accuracy;
    public String faceStatus;
    public java.util.List<Float> faceEmbedding;
    
    public PunchRequest(UUID employeeId, Double latitude, Double longitude, Float accuracy, String faceStatus) {
        this.employeeId = employeeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.faceStatus = faceStatus;
    }
    
    public void setFaceEmbedding(java.util.List<Float> faceEmbedding) {
        this.faceEmbedding = faceEmbedding;
    }
}
