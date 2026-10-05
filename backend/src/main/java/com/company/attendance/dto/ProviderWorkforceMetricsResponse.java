package com.company.attendance.dto;

import lombok.Data;

@Data
public class ProviderWorkforceMetricsResponse {
    private Long totalEmployees;
    private Long activeEmployees;
    private Long inactiveEmployees;
    private Long onLeaveEmployees;
}
