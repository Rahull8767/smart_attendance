package com.company.attendance.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class PunchRequest {
    private UUID employeeId;
    private Double latitude;
    private Double longitude;
    private Float accuracy;
    private String faceStatus;
    private List<Float> faceEmbedding; // The 192-dimensional vector from Android
}
