package com.company.attendance.dto;

import lombok.Data;

@Data
public class ProviderDashboardResponse {
    private Long totalCeoCount;
    private Long activeCeoCount;
    private Long inactiveCeoCount;
    private Long totalEmployeeCount;
    private Long activeEmployeeCount;
    private Long totalWorkSiteCount;
    private Double todayAttendanceRate; // Nullable if no data
    private Double faceVerificationRate; // Nullable if no data
    private Double locationVerificationRate; // Nullable if no data
}
