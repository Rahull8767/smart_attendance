package com.company.attendance.dto;

import lombok.Data;

@Data
public class ProviderAnalyticsResponse {
    private Long totalPunchIns;
    private Long faceVerificationSuccesses;
    private Long faceVerificationFailures;
    private Long locationVerificationSuccesses;
    private Long locationVerificationFailures;
}
